package com.example.juda.chatusernameandpassword;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import static com.example.juda.chatusernameandpassword.MainActivity.BASE_URL;

/**
 * Created by Juda on 24/12/2016.
 */

public class CheckMessageThread extends Thread {

    private OnNewMessageListener listener;
    //private String currentMessage = "";
    boolean go = true;
    List<Message> messages;
    String userName, password;


    public CheckMessageThread(OnNewMessageListener listener,
                              List<Message> messages, String userName, String password) {
        this.listener = listener;
        this.messages = messages;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void run() {
        while(go) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(BASE_URL +
                        "?action=check&username=" + userName + "&password=" + password + "&from="+messages.size());
                urlConnection =
                        (HttpURLConnection)
                                url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[512];
                int actuallyRead;
                StringBuilder stringBuilder =
                        new StringBuilder();
                while ((actuallyRead =
                        inputStream.read(buffer)) != -1) {
                    stringBuilder.append(
                            new String(buffer, 0, actuallyRead));
                }
                inputStream.close();
                inputStream = null;
                String msg = stringBuilder.toString();

                if(!msg.isEmpty()) {
                    String[] newMessages = msg.split("&");
                    for (String newMessage : newMessages) {
                        String[] contentAndSender = newMessage.split("~");
                        String content = URLDecoder.decode(contentAndSender[0], "utf-8");
                        messages.add(new Message(content, contentAndSender[1]));
                    }
                    if (listener != null)
                        listener.onNewMessage();
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopChecking(){
        go = false;
        interrupt();
    }

    interface OnNewMessageListener{
        void onNewMessage();
    }
}
