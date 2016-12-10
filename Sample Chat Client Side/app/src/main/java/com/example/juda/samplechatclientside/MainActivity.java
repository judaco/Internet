package com.example.juda.samplechatclientside;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://localhost:8080/MainServlet";
    EditText txtMessage;
    TextView lblMessage;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        lblMessage = (TextView) findViewById(R.id.txtMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
    }

    public void btnSend(View view) {
        btnSend.setEnabled(false);
        String message = txtMessage.getText().toString();
        if (message.isEmpty()){
            Toast.makeText(this, "please enter something . . .", Toast.LENGTH_SHORT).show();
            return;
        }
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {//the text of the user, thread makbili (but synchronized, and unsychronized to the main thread)
                String result = null;
                String message = params[0];
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL(BASE_URL +"action=send&message"
                    +message);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();
                    inputStream = urlConnection.getInputStream();
                    byte [] buffer = new byte[512];
                    int actuallyRead;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((actuallyRead = inputStream.read(buffer)) != -1){
                        stringBuilder.append(new String(buffer, 0, actuallyRead));
                    }
                    inputStream.close();
                    inputStream = null;
                    result = stringBuilder.toString();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute(message);
    }
}
