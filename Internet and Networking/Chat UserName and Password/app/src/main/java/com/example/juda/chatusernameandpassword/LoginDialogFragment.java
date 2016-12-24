package com.example.juda.chatusernameandpassword;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Juda on 24/12/2016.
 */

public class LoginDialogFragment extends DialogFragment {

    EditText txtUserName, txtPassword;
    Button btnLogin, btnSignup;
    LoginCompletedListener listener;
    String userName;
    String password;

    public void setListener(LoginCompletedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_login, null);
        txtUserName = (EditText)view.findViewById(R.id.txtUserName);
        txtPassword = (EditText)view.findViewById(R.id.txtPassword);
        btnLogin = (Button)view.findViewById(R.id.btnLogin);
        btnSignup = (Button)view.findViewById(R.id.btnSignup);
        btnLogin.setOnClickListener(loginClickListener);
        btnSignup.setOnClickListener(loginClickListener);


        return view;
    }



    private View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userName = txtUserName.getText().toString();
            password = txtPassword.getText().toString();
            if(userName.length() < 4 || password.length() < 3){
                Toast.makeText(getActivity(), "must enter valid username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            String action = v.getId() == R.id.btnLogin ? "login" : "signup";
            btnLogin.setEnabled(false);
            btnSignup.setEnabled(false);
            txtUserName.setEnabled(false);
            txtPassword.setEnabled(false);
            //TODO: please wait
            new AsyncTask<String, Void, Boolean>(){

                @Override
                protected Boolean doInBackground(String... params) {
                    boolean result = false;
                    String action = params[0];
                    HttpURLConnection urlConnection = null;
                    InputStream inputStream = null;
                    try{
                        URL url = new URL(MainActivity.BASE_URL
                                + "?action=" + action
                                + "&username="+userName
                                + "&password="+password);
                        urlConnection = (HttpURLConnection)url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setUseCaches(false);
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                        byte[] buffer = new byte[16];
                        int actuallyRead;
                        actuallyRead = inputStream.read(buffer);
                        inputStream.close();
                        inputStream = null;
                        if(actuallyRead != -1){
                            String response = new String(buffer, 0, actuallyRead);
                            if(response.equals("ok"))
                                result = true;
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
                protected void onPostExecute(Boolean result) {
                    btnLogin.setEnabled(true);
                    btnSignup.setEnabled(true);
                    txtUserName.setEnabled(true);
                    txtPassword.setEnabled(true);
                    if(result){
                        if(listener != null)
                            listener.onLogin(userName,password);
                        dismiss();
                    }else{
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(action);
        }
    };

    public interface LoginCompletedListener{
        void onLogin(String userName, String password);
    }
}
