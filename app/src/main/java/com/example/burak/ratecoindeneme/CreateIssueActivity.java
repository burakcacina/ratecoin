package com.example.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by burak on 16.02.2018.
 */
public class CreateIssueActivity extends AppCompatActivity {
    String USERID,result,c_description;;
    private Button mButton;
    private int edittextcount =0,iduser;
    EditText ET_DESC_OPTION,textIn;
    Button buttonAdd;
    LinearLayout container;
    TextView info;
    List<String> list = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createissue);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        iduser = prefs.getInt("USERID",-1);

        textIn = (EditText)findViewById(R.id.textin);
        buttonAdd = (Button)findViewById(R.id.add);
        container = (LinearLayout)findViewById(R.id.container);
        mButton = (Button)findViewById(R.id.createissue);

        ET_DESC_OPTION= (EditText)findViewById(R.id.desc_issue);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (Objects.equals(textIn.getText().toString(), "")) {
                    Toast.makeText(getApplicationContext(), "Fill option!!", Toast.LENGTH_LONG).show();
                } else {
                    if (edittextcount <= 3) {
                        LayoutInflater layoutInflater =
                                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addView = layoutInflater.inflate(R.layout.row, null);
                        final TextView textOut = (TextView) addView.findViewById(R.id.textout);

                        list.add(edittextcount, textIn.getText().toString());

                        textOut.setId(edittextcount);
                        textOut.setText(list.get(edittextcount));

                        System.out.println(list.get(edittextcount));

                        Button buttonRemove = (Button) addView.findViewById(R.id.remove);
                        final View.OnClickListener thisListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((LinearLayout) addView.getParent()).removeView(addView);
                                edittextcount = edittextcount - 1;
                                list.remove(edittextcount);
                                System.out.println(edittextcount);
                            }
                        };
                        buttonRemove.setOnClickListener(thisListener);
                        container.addView(addView);

                        textIn.setText("");
                        edittextcount = edittextcount + 1;
                        System.out.println(edittextcount);
                        textOut.setKeyListener(null);
                    } else {
                        Toast.makeText(getApplicationContext(), "No more options!!", Toast.LENGTH_LONG).show();
                    }
                }
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
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;
            String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/issue_create.php?id=" +iduser;

            try {
                URL url = new URL(URL_TO_HIT);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect(); //Create JSONObject here JSONObject

                final JSONObject jsonParam = new JSONObject();

                c_description =ET_DESC_OPTION.getText().toString();


                for(int i=0; i<list.size(); i++)
                {
                    if(!list.get(i).isEmpty())
                    {
                        int sum = i+1;
                        jsonParam.put("option_"+sum,list.get(i));
                    }
                }

                jsonParam.put("description",c_description);


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
    @Override
    public void onBackPressed() {
        Intent intentUpdate = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentUpdate);
        this.finish();

    }
}