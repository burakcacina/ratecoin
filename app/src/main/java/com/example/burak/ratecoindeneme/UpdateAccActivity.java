package com.example.burak.ratecoindeneme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/* This is Created
        by
      BURAK CACINA
*/

public class UpdateAccActivity extends AppCompatActivity {

    EditText ET_USER_REG_PASS,ET_REG_USER_REG_PASS_CONF;
    String user_pass,user_pass_conf,error,error2;
    int iduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Button but1 = (Button) findViewById(R.id.userUpp);
        Button but2 = (Button) findViewById(R.id.deleteAccountbutton);

        ET_USER_REG_PASS= (EditText)findViewById(R.id.user_pass);
        ET_REG_USER_REG_PASS_CONF = (EditText) findViewById(R.id.user_pass_confirmation);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        iduser = prefs.getInt("USERID",-1);

        final String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/account_update.php?id=" + iduser;
        System.out.println(URL_TO_HIT);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask().execute(URL_TO_HIT);
            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UpdateAccActivity.this, DeleteUserActivity.class);
                startActivity(intent);
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

            String reg_url = "http://localapi25.atwebpages.com/android_connect/account_update.php?id=" + iduser;
            System.out.println(reg_url);
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(reg_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect(); //Create JSONObject here JSONObject

                user_pass =ET_USER_REG_PASS.getText().toString();
                user_pass_conf= ET_REG_USER_REG_PASS_CONF.getText().toString();

                if (!user_pass.equals(user_pass_conf)) {
                    System.out.println("PASSWORD NOT EQUAL");
                    error2 = "not match";                }
                else {
                    String data2 = URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(user_pass, "UTF-8");
                    OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    out.write(data2.toString());
                    out.close();
                }
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

                    } else {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        JSONObject jsonObj = new JSONObject(sb.toString());
                        error = jsonObj.getString("message");
                        return error;
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

        protected void onPostExecute(String error) {
            if (error != null && error2 != null) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), error2, Toast.LENGTH_LONG).show();

            }
            else  {
                Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Login Screen", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UpdateAccActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
    }

}
