package example.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
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
        if(actuallyRead == -1) {
            inputStream.close();
            outputStream.close();
            return;
        }
        String requestFromClient = new String(buffer, 0, actuallyRead);

        //{ name:"snoopy", age:105, owner:{firstName:"John",lastName:"Smith"}, friends:["Tom","Jerry"]  }


        HashMap<String, String> qs = processQueryString(new String(buffer, 0, actuallyRead));
        try {
            int result = calculate(qs);
            //outputStream.write(("result="+result).getBytes());
            byte[] resultBytes = new byte[Integer.BYTES];
            ByteBuffer.wrap(resultBytes).putInt(result);
            outputStream.write(resultBytes);
        }catch (InvalidParameterException ex){
            outputStream.write("invalid".getBytes());
        }
        outputStream.close();
        inputStream.close();
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String query = request.getQueryString();
        if(query == null || query.isEmpty())
            return;
        HashMap<String, String> qs = processQueryString(query);

        try {
            response.getWriter().write("result=" + calculate(qs));
        }catch (InvalidParameterException ex){
            response.getWriter().write("invalid");
        }
    }

    private int calculate(HashMap<String, String> qs) throws InvalidParameterException{
        String num1String = qs.get("num1");
        if(num1String.isEmpty())
            throw new InvalidParameterException();
        String num2String = qs.get("num2");
        if(num2String.isEmpty())
            throw new InvalidParameterException();
        int num1=0, num2=0;
        try{
            num1 = Integer.valueOf(num1String);
            num2 = Integer.valueOf(num2String);
        }catch (Exception ex){
            throw new InvalidParameterException();
        }
        String operator = qs.get("operator");
        if(operator.isEmpty())
            throw new InvalidParameterException();
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
                if(num2 == 0)
                    throw new InvalidParameterException();
                result = num1 / num2;
                break;
            default:
                throw new InvalidParameterException();
        }
        return result;
    }

    private HashMap<String, String> processQueryString(String query){
        HashMap<String, String> qs = new HashMap<>();
        String[] keyValuePairs = query.split("&");
        for(String keyValuePair : keyValuePairs){
            String[] keyAndValue = keyValuePair.split("=");
            if(keyAndValue.length != 2)
                continue;
            qs.put(keyAndValue[0], keyAndValue[1]);
        }
        return qs;
    }
}

//  5*10^2 + 7*10^1  + 8*10^0
//578

//0*256^3 + 0*256^2 + 1*256^1 + 2*256^0
//0000000000    0
//0000000001    1
//0000000010    2
//0000000011    3
//0000000100    4
//0000000101    5
//0000000110    6
//0000000111    7
//   0*2^3  +  1*2^2  + 1*2^1  + 1*2^0
//
            /*int x = 258;
            byte[] xBytes = new byte[4];
            xBytes[3] = (byte)x;
            xBytes[2] = (byte)(x >> 8);
            xBytes[1] = (byte)(x >> 16);
            xBytes[0] = (byte)(x >> 24);
            int y = ((int)xBytes[0])<<24;
            y = y | ((int)xBytes[1])<<16;
            y = y | ((int)xBytes[2])<<8;
            y = y | ((int)xBytes[3]);*/

