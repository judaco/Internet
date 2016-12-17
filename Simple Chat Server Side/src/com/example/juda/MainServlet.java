package com.example.juda;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Juda on 17/12/2016.
 */
public class MainServlet extends javax.servlet.http.HttpServlet {

    //private String message;
    private List<String> messages;
    private Map<String, String> users;


    @Override
    public void init() throws ServletException {//set once, first request to any client from the Server - will set up to all http requests - used as constructor
        //message = "no message";
        messages = new ArrayList<>();
        users = new HashMap<>();
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
    //what is a URL (uniform resource locator)
    //http://www.mywebsite.com:80/path/path?key1=value1&key2=value2
    //http://www.mywebsite.com:80/path/path?action=send&message=hello
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        //String ipAddress = request.getRemoteAddr();

        String query = request.getQueryString();//pull the query string request
        if (query == null || query.isEmpty())//if the first it's true i don't check the second one
            return;
        Map<String, String> qs = new HashMap<>();//collection of generic key values (n,n ==> string), but accepted unique key
        String[] keyValues = query.split("&");//split the String to substring in the array String, by the &
        for (String keyValue : keyValues) {//pass over all the couple of the key values
            String[] keyAndValue = keyValue.split("=");
            if (keyAndValue.length != 2)
                continue;//stop the iteration of the for loop, in order to move the next statement or expression
            qs.put(keyAndValue[0], keyAndValue[1]);
        }//here I will ask all the questions = turn to Server - I am asking the String in the serverS
        String action = qs.get("action");//pull from query string by key action
        if (action == null)
            return;
        String userName, password;
        userName = qs.get("username");
        if (userName == null || userName.length() < 4)
            return;
        password = qs.get("password");
        if (password == null || password.length() < 4)
            return;
        switch (action) {
            case "signup":
                boolean success = false;//will the sign up will succeed or not
                synchronized (users){
                    if (!users.containsKey(userName)){
                        users.put(userName, password);
                        success = true;
                    }
                }
                response.getWriter().write(success ? "ok" : "error");
                break;
            case "send":
                String newMessage = qs.get("message");//pull by key message
                if (newMessage != null) {
                    //this.message = newMessage;//for String
                    messages.add(newMessage);//for ListView
                    response.getWriter().write("OK");
                }
                break;
            case "check":
                    String from = qs.get("from");
                    if (from != null){
                        try {
                            int fromMessage = Integer.valueOf(from);
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = fromMessage; i < messages.size(); i++) {
                                stringBuilder.append(messages.get(i)+ "&");
                            }
                            if (stringBuilder.length() > 0)
                                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                response.getWriter().write(stringBuilder.toString());
        }catch (Exception ex){

             }
           }
       break;
      }
    }
}
