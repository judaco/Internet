package com.example.juda.simplechatclientserver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CheckMessageThread.OnNewMessageListener {

    public static final String BASE_URL = "http://146.148.28.47/SimpleChat/MainServlet";
    private EditText txtMessage;
    //private TextView lblMessage;
    private Button btnSend;
    private  CheckMessageThread checkMessageThread;
    Handler handler = new Handler();
    ListView listMessages;//for the new incoming messages
    List<String> messages;
    ArrayAdapter<String> adapter;//we will use the built adapter, when we will have our own chat with
    //password and user names we will create a new Adapter.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        //lblMessage = (TextView) findViewById(R.id.txtMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        listMessages = (ListView)findViewById(R.id.listMessages);
        messages = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        listMessages.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMessageThread == null){
            checkMessageThread = new CheckMessageThread(this);
            checkMessageThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkMessageThread != null){
            checkMessageThread.stopChecking();
            checkMessageThread = null;
        }
    }

    public void btnSend(View view) {
        btnSend.setEnabled(false);
        String message = txtMessage.getText().toString();
        if (message.isEmpty()){
            Toast.makeText(this, "please enter something . . .", Toast.LENGTH_SHORT).show();
            return;
        }
        new AsyncTask<String, Void, String>() {//if my mission is running once, if i have a continuing process i will use a thread
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
                    urlConnection.setRequestMethod("GET");//get is the default
                    urlConnection.setUseCaches(false);//don't remember the response of the server
                    urlConnection.connect();//connect to the server
                    inputStream = urlConnection.getInputStream();//stream who get bytes
                    byte [] buffer = new byte[512];
                    int actuallyRead;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((actuallyRead = inputStream.read(buffer)) != -1){
                        stringBuilder.append(new String(buffer, 0, actuallyRead));
                    }
                    inputStream.close();
                    inputStream = null;//the garabage collector remove it
                    result = stringBuilder.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (inputStream != null)//I would like to close the input stream, I don't want a re-closing, so I close the input stream in the finally
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
                return result;//I return result to ASync Task, and he run the onPostExecute (method)
            }

            @Override
            protected void onPostExecute(String result) {
                btnSend.setEnabled(true);//release the button
                if (result.equals("ok")){
                    txtMessage.setText("");
                }
            }
        }.execute(message);//my message go to  my doinback method
    }

    @Override
    public void onNewMessage(String newMessage) {//this is the message which I will see in the future as msg
        handler.post(new NewMessageRunnable(newMessage) {
            @Override
            public void run() {//this message will be in the future
                lblMessage.setText(msg);
            }
        });
    }
    static abstract class NewMessageRunnable implements Runnable{//using an abstract class in order not to implement the method "run"

        String msg;

        public NewMessageRunnable(String msg){
            this.msg = msg;
        }

    }
}
