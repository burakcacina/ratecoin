package com.rtc.burak.ratecoindeneme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.os.health.SystemHealthManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private final String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/account_create.php";
    EditText ET_USER_REG_NAME,ET_USER_REG_PASS,ET_USER_REG_MAIL;
    String user_name,user_pass,user_mail,result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button but1 = (Button) findViewById(R.id.userReg);
        ET_USER_REG_NAME= (EditText)findViewById(R.id.new_user_name);
        ET_USER_REG_PASS = (EditText)findViewById(R.id.new_user_pass);
        ET_USER_REG_MAIL = (EditText)findViewById(R.id.new_user_mail);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask().execute(URL_TO_HIT);
            }
        });

    }

    public class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String reg_url = "http://localapi25.atwebpages.com/android_connect/account_create.php";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(reg_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect(); //Create JSONObject here JSONObject

                JSONObject jsonParam = new JSONObject();

                user_pass =ET_USER_REG_PASS.getText().toString();
                user_name =ET_USER_REG_NAME.getText().toString();
                user_mail =ET_USER_REG_MAIL.getText().toString();

                jsonParam.put("password", user_pass);
                jsonParam.put("username", user_name);
                jsonParam.put("mail", user_mail);

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                System.out.println(jsonParam);
                out.write(jsonParam.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();
                System.out.println(HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
                else if(HttpResult == HttpURLConnection.HTTP_NOT_AUTHORITATIVE)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    result = jsonObj.getString("message");
                    System.out.println(result);
                    return result;
                }
                else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println(sb.toString());
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    result = jsonObj.getString("message");
                    System.out.println(result);
                    return result;
                }

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }

        public class Response {
            public String response_notmatch;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
            else  {
                Toast.makeText(getApplicationContext(), "Account Created / Check your mail!", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Login Screen", Toast.LENGTH_LONG).show();
            }
        }
    }
}
