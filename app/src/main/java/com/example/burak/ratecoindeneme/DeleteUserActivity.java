package com.example.burak.ratecoindeneme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class DeleteUserActivity extends AppCompatActivity {
    String access_token,response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int iduser = prefs.getInt("USERID",-1);

        final String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/account_delete.php?id="+iduser;
        new JSONTask().execute(URL_TO_HIT);

    }
    public class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();
                System.out.println(HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    response = "DELETED";
                    return response;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }  finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(response != null)
            {
                Intent intent = new Intent(DeleteUserActivity.this, MainActivity.class);
                Toast.makeText(getApplicationContext(), "Deleted Succesfully", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Recipe Page", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }
}
