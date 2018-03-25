package com.example.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

/**
 * Created by burak on 16.02.2018.
 */
public class CreateIssueActivity extends AppCompatActivity {
    String USERID,result;
    String c_option1,c_option2,c_option3,c_option4,c_option5,c_description;

    private LinearLayout mLayout;
    private EditText mEditText;
    private Button mButton;
    private int edittextcount =1;
    EditText ET_DESC_OPTION;
    int iduser;

    //yavuz

    //pull

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createissue);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        iduser = prefs.getInt("USERID",-1);

        ET_DESC_OPTION= (EditText)findViewById(R.id.desc_issue);

        mLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.editText);

        final String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/issue_create.php?id=" +iduser;

        Button but1 = (Button) findViewById(R.id.createissue);


        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask().execute();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                    mLayout.addView(createNewTextView(mEditText.getText().toString()));
                    mLayout.setId(edittextcount);
                    final LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    Button btnTag = new Button(CreateIssueActivity.this);
                    btnTag.setLayoutParams(lparams2);
                    btnTag.setText("-");
                    btnTag.setId(edittextcount+10);

                    btnTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mLayout.removeAllViews();
                            edittextcount = 1;
                        }
                    });

                    mLayout.addView(btnTag);
                    mEditText.setText("");
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

            String reg_url = "http://localapi25.atwebpages.com/android_connect/issue_create.php?id=" +iduser;
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(reg_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect(); //Create JSONObject here JSONObject

                final JSONObject jsonParam = new JSONObject();

                c_description =ET_DESC_OPTION.getText().toString();

                jsonParam.put("description", c_description);
                jsonParam.put("option_1", c_option1);
                jsonParam.put("option_2", c_option2);
                jsonParam.put("option_3", c_option3);
                jsonParam.put("option_4", c_option4);

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                System.out.println(jsonParam.toString());
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

                    Intent intent = new Intent(CreateIssueActivity.this, HomeActivity.class);
                    startActivity(intent);

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
                Toast.makeText(getApplicationContext(), "Succesfully Created", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Home Screen", Toast.LENGTH_LONG).show();
            }
        }
    }
    @SuppressLint("ResourceType")
    private EditText createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        EditText textView = new EditText(this);

        lparams.setMargins(230,20,0,-120);
        textView.setLayoutParams(lparams);
        textView.setWidth(710);
        textView.setHint("Options  " + edittextcount);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setId(edittextcount);
        System.out.println(textView.getId());
        if(textView.getId() == 1)
        {
            c_option1 = textView.getText().toString();
        }
        if(textView.getId() == 2)
        {
            c_option2 = textView.getText().toString();
        }
        if(textView.getId() == 3)
        {
            c_option3 = textView.getText().toString();

        }
        if(textView.getId() == 4)
        {
            c_option4 = textView.getText().toString();
        }
        edittextcount = edittextcount+1;

        return textView;
    }

}