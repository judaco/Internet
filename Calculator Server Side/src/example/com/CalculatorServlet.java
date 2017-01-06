package example.com;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by Juda on 06/01/2017.
 */
public class CalculatorServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        InputStream inputStream = request.getInputStream();
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[64];
        int actuallyRead = inputStream.read(buffer);
        if (actuallyRead == -1) {
            inputStream.close();
            outputStream.close();
            return;
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String query = request.getQueryString();
        if (query == null || query.isEmpty())
            return;
        HashMap<String, String> qs = new HashMap<>();
        String[] keyValuePairs = query.split("&");
        for (String keyValuePair : keyValuePairs){
            String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue.length != 2)
                continue;
            qs.put(keyAndValue[0], keyAndValue[1]);//0-the key, 1-the value
        }
        String num1String = qs.get("num1");
        if (num1String.isEmpty())
            return;
        String num2String = qs.get("num2");
        if (num2String.isEmpty())
            return;
        int num1 = 0, num2 = 0;
        try {
            num1 = Integer.valueOf(num1String);
            num2 = Integer.valueOf(num2String);
        }catch (Exception ex){
            return;
        }
        String operator = qs.get("operator");
        if (operator.isEmpty())
            return;
        int result = 0;
        switch (operator){
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
                if (num2 == 0)
                    return;
                result = num1 / num2;
                break;
            default:
                return;
        }
        response.getWriter().write("result" + result);
    }
}
