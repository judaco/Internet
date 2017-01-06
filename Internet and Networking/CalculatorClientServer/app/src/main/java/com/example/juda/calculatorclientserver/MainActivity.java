package com.example.juda.calculatorclientserver;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    TextView lblResult;
    EditText txtNum1, txtNum2;
    boolean working = false;
    //Button btnPlus, btnMinus, btnMultiply, btnDivide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblResult = (TextView)findViewById(R.id.lblResult);
        txtNum1 = (EditText)findViewById(R.id.txtNum1);
        txtNum2 = (EditText)findViewById(R.id.txtNum2);
    }

    public void btnCalculate(View view) {
        if (working){
            Toast.makeText(this, "please wait for previous calculation to finish", Toast.LENGTH_SHORT).show();
            return;
        }
        String num1String = txtNum1.getText().toString();
        String num2String = txtNum2.getText().toString();
        if (num1String.isEmpty() || num2String.isEmpty()){
            Toast.makeText(this, "please enter numbers first . . .", Toast.LENGTH_SHORT).show();
            return;
        }
        int num1 = 0;
        int num2 = 0;
        try {
            num1 = Integer.valueOf(num1String);
            num2 = Integer.valueOf(num2String);
        }catch (Exception ex){
            Toast.makeText(this, "enter integers only", Toast.LENGTH_SHORT).show();
        }
        Integer operator = Integer.valueOf((String)view.getTag());

        working = true;
        lblResult.setText("please wait..");
        new AsyncTask<Integer, Void, Integer>() {
            @Override
            protected Integer doInBackground(Integer... params) {
                Integer result = null;
                int num1 = params[0];
                int num2 = params[1];
                int operator = params[2];
                String operatorString = "";
                switch (operator){
                    case  1:
                        operatorString = "plus";
                        break;
                    case 2:
                        operatorString = "minus";
                        break;
                    case 3:
                        operatorString = "multiply";
                        break;
                    case 4:
                        operatorString = "divide";
                        break;
                }
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL("http://10.0.2.2:8080/CalculatorServlet?num1="
                            + num1 + "&num2=" + num2 + "&operator=" + operatorString);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();
                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[64];
                    int actuallyRead = inputStream.read(buffer);
                    inputStream.close();
                    inputStream = null;
                    if(actuallyRead != -1){
                        String response = new String(buffer, 0, actuallyRead);
                        String[] responseKeyValue = response.split("=");
                        if(responseKeyValue.length == 2){
                            try{
                                result = Integer.valueOf(responseKeyValue[1]);
                            }catch (Exception ex){
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                working = false;
                if(result != null){
                    lblResult.setText(String.valueOf(result));
                }else{
                    lblResult.setText("error");
                }
            }
        }.execute(num1, num2, operator);
    }
}
