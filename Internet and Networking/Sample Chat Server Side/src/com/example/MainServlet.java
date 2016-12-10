package com.example;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juda on 10/12/2016.
 */
public class MainServlet extends javax.servlet.http.HttpServlet {

    private String message;

    @Override
    public void init() throws ServletException {//set once, first request to any client from the Server - will set up to all http requests - used as constructor
        message = "no message";
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
    //http://www.111.com:8080/path/path(MainServlet)?key1=value1&key2=value2
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String query = request.getQueryString();
        if (query == null || query.isEmpty())//if the first it's true i dont check the second one
            return;
        Map<String, String> qs = new HashMap<>();//collection of generic key values (n,n ==> string), but accepted unique key
        String[] keyValues = query.split("&");
        for (String keyValue : keyValues) {//pass over all the couple of the key values
            String[] keyAndValue = keyValue.split("=");
            if (keyAndValue.length != 2)
                continue;//stop the iteration of the for loop
            qs.put(keyAndValue[0], keyAndValue[1]);
        }//here I will ask all the questions = turn to Server - I am asking the String in the serverS
        String action = qs.get("action");
        if (action == null)
            return;
        switch (action) {
            case "send":
                String newMessage = qs.get("message");
                if (newMessage != null)
                    this.message = newMessage;
                response.getWriter().write("OK");
                break;
            case "check":
                response.getWriter().write(this.message);
                break;
        }
    }
}
