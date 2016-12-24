package com.example.juda.chatusernameandpassword;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CheckMessageThread.OnNewMessageListener, LoginDialogFragment.LoginCompletedListener {

    public static final String BASE_URL = "http://146.148.28.47/SimpleChat/MainServlet";
    //public static final String BASE_URL = "http://10.0.2.2:8080/MainServlet";
    private EditText txtMessage;
    //private TextView lblMessage;
    private Button btnSend;
    private CheckMessageThread checkMessageThread;
    Handler handler = new Handler();
    ListView listMessages;
    List<Message> messages;
    MessagesAdapter adapter;
    String userName, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMessage = (EditText)findViewById(R.id.txtMessage);
        //lblMessage = (TextView)findViewById(R.id.lblMessage);
        btnSend = (Button)findViewById(R.id.btnSend);
        listMessages = (ListView)findViewById(R.id.listMessages);
        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages);
        listMessages.setAdapter(adapter);
        LoginDialogFragment fragment = new LoginDialogFragment();
        fragment.setListener(this);
        fragment.setCancelable(false);
        fragment.show(getFragmentManager(), "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startThread();
    }

    private void startThread(){
        if(checkMessageThread == null && userName != null){
            checkMessageThread =
                    new CheckMessageThread(this, messages, userName, password);
            checkMessageThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(checkMessageThread != null){
            checkMessageThread.stopChecking();
            checkMessageThread = null;
        }
    }

    public void btnSend(View view) {
        btnSend.setEnabled(false);
        btnSend.setText(R.string.sending);
        String message = txtMessage.getText().toString();
        if(message.isEmpty()){
            Toast.makeText(this, "please enter something...", Toast.LENGTH_SHORT).show();
            btnSend.setEnabled(true);
            btnSend.setText(R.string.sending);
            return;
        }
        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... params) {
                String result = null;
                String message = params[0];
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                try {
                    message = URLEncoder.encode(message, "utf-8");
                    URL url = new URL(
                            BASE_URL +
                                    "?action=send&username=" + userName + "&password=" + password +
                                    "&message=" + message);
                    urlConnection =
                            (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();
                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[512];
                    int actuallyRead;
                    StringBuilder stringBuilder = new StringBuilder();
                    while((actuallyRead = inputStream.read(buffer)) != -1){
                        stringBuilder.append(new String(buffer, 0, actuallyRead));
                    }
                    inputStream.close();
                    inputStream = null;
                    result = stringBuilder.toString();
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
            protected void onPostExecute(String result) {
                btnSend.setEnabled(true);
                btnSend.setText(R.string.sending);
                if(result.equals("ok")){
                    txtMessage.setText("");
                }
            }
        }.execute(message);


    }

    @Override
    public void onNewMessage() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                listMessages
                        .smoothScrollToPosition(messages.size());
            }
        });

    }

    @Override
    public void onLogin(String userName, String password) {
        this.userName = userName;
        this.password = password;
        adapter.setUserName(userName);
        startThread();
    }

    /*static abstract class NewMessageRunnable implements Runnable{
        String msg;
        public NewMessageRunnable(String msg) {
            this.msg = msg;
        }
    }*/
}
