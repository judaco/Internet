package example.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Juda on 07/01/2017.
 */
public class MainServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        InputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[1024];
        int actuallyRead;
        StringBuilder stringBuilder = new StringBuilder();
        while((actuallyRead = inputStream.read(buffer)) != -1){
            stringBuilder.append(new String(buffer, 0, actuallyRead));
        }
        try {
            JSONObject jsonRequest =
                    new JSONObject(stringBuilder.toString());
            int num1 = jsonRequest.getInt("num1");
            int num2 = jsonRequest.getInt("num2");
            String operator = jsonRequest.getString("operator");
            JSONObject jsonDog1 = jsonRequest.getJSONObject("dog1");
            String dogName = jsonDog1.getString("name");
            int dogAge = jsonDog1.getInt("age");
            Dog d1 = new Dog(dogName, dogAge);
            JSONArray jsonDogs = jsonRequest.getJSONArray("dogs");
            for (int i = 0; i < jsonDogs.length(); i++) {
                JSONObject jsonDog = jsonDogs.getJSONObject(i);


            }

            Dog d2 = new Dog("snoopy", 105);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("name", d2.name);
            jsonResponse.put("age", d2.age);
            jsonResponse.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
class Dog{
    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    String name;
    int age;
}