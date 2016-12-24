package com.example.juda.httppostrequest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try{
                    URL url = new URL("http://10.0.2.2:8080/MainServlet");
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setUseCaches(false);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    inputStream = urlConnection.getInputStream();
                    outputStream = urlConnection.getOutputStream();
                    outputStream.write("test".getBytes());
                    byte[] buffer = new byte[256];
                    int actuallyRead;
                    actuallyRead = inputStream.read(buffer);
                    outputStream.close();
                    outputStream = null;
                    inputStream.close();
                    inputStream = null;
                    if(actuallyRead != -1) {
                        String response = new String(buffer, 0, actuallyRead);
                        Log.d("Elad", "response=" + response);
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
                    if(outputStream != null)
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();
    }
}