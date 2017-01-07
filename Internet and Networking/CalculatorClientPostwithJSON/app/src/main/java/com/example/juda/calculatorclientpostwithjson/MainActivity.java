package com.example.juda.calculatorclientpostwithjson;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    EditText txtNum1, txtNum2;
    TextView lblResult;
    boolean isCalculating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtNum1 = (EditText)findViewById(R.id.txtNum1);
        txtNum2 = (EditText)findViewById(R.id.txtNum2);
        lblResult = (TextView)findViewById(R.id.lblResult);


    }

    public void btnCalculate(View view) {
        if(isCalculating){
            Toast.makeText(this,
                    "please wait for previous calculation to complete",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String operator = (String)view.getTag();
        String num1 = txtNum1.getText().toString();
        String num2 = txtNum2.getText().toString();
        if(num1.isEmpty() || num2.isEmpty()){
            Toast.makeText(this,
                    "please enter some number...",
                    Toast.LENGTH_SHORT).show();
        }
        if(operator.equals("divide") && num2.equals("0")){
            Toast.makeText(this, "you may not divide by zero", Toast.LENGTH_SHORT).show();
            return;
        }
        isCalculating = true;
        lblResult.setText("please wait...");
        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... params) {
                String result = "";
                OutputStream outputStream = null;
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;
                int num1 = Integer.valueOf(params[0]);
                int num2 = Integer.valueOf(params[1]);
                String operator = params[2];
                try{
                    URL url = new URL("http://10.0.2.2:8080/CalculatorServlet");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();
                    outputStream = urlConnection.getOutputStream();
                    JSONObject jsonRequest = new JSONObject();
                    jsonRequest.put("num1", num1);
                    jsonRequest.put("num2", num2);
                    jsonRequest.put("operator", operator);
                    outputStream.write(jsonRequest.toString().getBytes());
                    if(urlConnection.getResponseCode() == 200){
                        inputStream = urlConnection.getInputStream();
                        byte[] buffer = new byte[256];
                        int actuallyRead;
                        StringBuilder stringBuilder = new StringBuilder();
                        int counter = 0;
                        while ((actuallyRead = inputStream.read(buffer)) != -1){
                            stringBuilder.append(new String(buffer, 0, actuallyRead));
                            publishProgress(counter++);
                        }
                        JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                        result = jsonResponse.getString("result");
                    }else{
                        Log.d("Elad", "error code = " + urlConnection.getResponseCode());
                        /*inputStream = urlConnection.getErrorStream();
                        byte[] buffer = new byte[1024];
                        int actuallyRead;
                        actuallyRead = inputStream.read(buffer);
                        if(actuallyRead != -1)
                            result = new String(buffer, 0, actuallyRead);*/
                        result = "error " + urlConnection.getResponseCode();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if(inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if(outputStream != null)
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
                publishProgress(10);
                return result;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result) {
                isCalculating = false;
                lblResult.setText(result);
            }
        }.execute(num1, num2, operator);

    }
}
