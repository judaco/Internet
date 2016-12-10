package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Juda on 03/12/2016.
 */

public class MainServlet extends javax.servlet.http.HttpServlet {

    int counter = 0;

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        InputStream inputStream = request.getInputStream();
        int actuallyRead;
        byte[] buffer = new byte[64];
        StringBuilder stringBuilder = new StringBuilder();
        while ((actuallyRead = inputStream.read(buffer)) != -1){
            stringBuilder.append(
                    new String(buffer, 0, actuallyRead));
        }
        inputStream.close();
        System.out.println(stringBuilder.toString());
        OutputStream outputStream = response.getOutputStream();
        outputStream.write("נכון היום יום רביעי".getBytes());
        outputStream.close();

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        System.out.println(counter++);
        response.getWriter().write("counter="+counter);

        String qs = request.getQueryString();
        /*if(qs != null){
            Map<String, String> queryString = new HashMap<>();
            String[] keyValuesString = qs.split("&");
            for (String keyValueString : keyValuesString){
                String[] keyValuePair =
                        keyValueString.split("=");
                if(keyValuePair.length != 2)
                    continue;
                queryString.put(keyValuePair[0],
                        keyValuePair[1]);
            }
            if(queryString.containsKey("key1")){
                String value1 = queryString.get("key1");
                System.out.println(value1);
            }
        }*/
        System.out.println(qs);
    }

}
