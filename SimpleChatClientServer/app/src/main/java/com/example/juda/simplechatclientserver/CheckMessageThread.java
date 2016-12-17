package com.example.juda.simplechatclientserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import static com.example.juda.simplechatclientserver.MainActivity.BASE_URL;

/**
 * Created by Juda on 17/12/2016.
 */

public class CheckMessageThread extends Thread {

    OnNewMessageListener listener;
    boolean go = true;
    private String currentMessage = "";//the server has the same String, and I want to know if the mesage has been changes, so I won't call again to the listener
    CheckMessageThread(OnNewMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (go) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(BASE_URL + "?action=check");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[512];
                int actuallyRead;
                StringBuilder stringBuilder = new StringBuilder();
                while ((actuallyRead = inputStream.read(buffer)) != -1) {
                    stringBuilder.append(new String(buffer, 0, actuallyRead));
                }
                inputStream.close();
                inputStream = null;
                String msg = stringBuilder.toString();//the message the clinet sent
                msg = URLDecoder .decode(msg, "utf-8");
                if (!currentMessage.equals(msg)) {
                    if (listener != null)
                        listener.onNewMessage(msg);
                    currentMessage = msg;//save  the message
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void stopChecking(){
        go = false;
        interrupt();
    }
    interface OnNewMessageListener{
        void onNewMessage(String newMessage);
    }
}