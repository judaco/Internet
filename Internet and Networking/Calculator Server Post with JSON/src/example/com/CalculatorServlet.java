package example.com;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Juda on 07/01/2017.
 */
public class CalculatorServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try (
                InputStream inputStream = request.getInputStream();
                OutputStream outputStream = response.getOutputStream()) {
                byte [] buffer = new byte[256];
                StringBuilder stringBuilder = new StringBuilder();
                int actuallyRead;
                while ((actuallyRead = inputStream.read(buffer)) != -1){
                    stringBuilder.append(new String(buffer, 0, actuallyRead));
                }
            JSONObject jsonRequest = new JSONObject(stringBuilder.toString());
                int num1 = jsonRequest.getInt("num1");
                int num2 = jsonRequest.getInt("num2");
                String operator = jsonRequest.getString("operator");
                JSONObject jsonResponse = new JSONObject();
                int result = 0;
            switch (operator) {
                case "plus":
                    result = num1 + num2;
                    break;
                case "minus":
                    result = num1 - num2;
                    break;
                case "multiply":
                    result = num1 * num2;
                    break;
                case "divide":
                    if (num2 != 0)
                        result = num1 / num2;
                    else {
                        response.sendError(409, "division by zero");

                    }
                    break;
            }
            jsonResponse.put("result", result);
            outputStream.write(jsonResponse.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
