package com.example.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class UserDetailsActivity extends AppCompatActivity {
    int iduser;
    private TextView tvwallet;
    private TextView tvmail;
    private TextView tvUsername;
    private ImageView singleImage;
    private Bitmap bitmap;


    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinformation);

        singleImage = (ImageView) findViewById(R.id.image1);
        tvUsername = (TextView) findViewById(R.id.tvuserName);
        tvmail = (TextView) findViewById(R.id.tvmail);
        tvwallet = (TextView) findViewById(R.id.tvwallet);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        iduser = prefs.getInt("USERID",-1);

        final String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/account_details.php?id="+iduser;
        new JSONTask().execute(URL_TO_HIT);

        Button but1 = (Button) findViewById(R.id.changeuserphoto);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(UserDetailsActivity.this);
                dialog.setContentView(R.layout.custom_dialog_box);
                dialog.setTitle("Alert Dialog View");
                Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btnChoosePath)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                Intent intent = new Intent(UserDetailsActivity.this, PhotoActivity.class);
                                intent.putExtra("result", 1);  // pass your values and retrieve them in the other Activity using keyName
                                startActivity(intent);
                            }
                        });
                dialog.findViewById(R.id.btnTakePhoto)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                if (shouldAskPermissions()) {
                                    askPermissions();
                                }
                                askPermissions();
                                Intent intent = new Intent(UserDetailsActivity.this, PhotoActivity.class);
                                intent.putExtra("result", 2);  // pass your values and retrieve them in the other Activity using keyName
                                startActivity(intent);
                            }
                        });

                // show dialog on screen
                dialog.show();
            }
        });

    }
    public class JSONTask extends AsyncTask<String, Void, JSONTask.Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Response doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;
            Response r = new Response();

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();
                System.out.println(HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    JSONObject myJson = new JSONObject(sb.toString());
                    r.response_userid = myJson.optInt("id");
                    r.response_username = myJson.optString("name");
                    r.response_usermail = myJson.optString("mail");
                    r.response_userimage =myJson.optString("image");

                    InputStream in = new java.net.URL(r.response_userimage).openStream();
                    bitmap = BitmapFactory.decodeStream(in);

                    System.out.println(r.response_userimage);
                    System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());

                    return r;
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
            public String response_username;
            public int response_userid;
            public String  response_usermail;
            public String  response_userimage;

        }
        @SuppressLint("SetTextI18n")
        protected void onPostExecute(final Response r) {
            tvUsername.setText("User: " + r.response_username);
            tvmail.setText("Mail: " + String.valueOf(r.response_usermail));
            singleImage.setImageBitmap(rotate(bitmap,90));
        }
    }
    private Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Bitmap bmOut;
            Matrix matrix = new Matrix();
            if (bm.getWidth() == bm.getHeight() )
            {
                matrix.postRotate(90);
                bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bmOut = BITMAP_RESIZER(bmOut);
            }
            else if(bm.getWidth() > bm.getHeight())
            {
                matrix.postRotate(-90);
                bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bmOut = BITMAP_RESIZER(bmOut);
            }
            else {
                bmOut = BITMAP_RESIZER(bm);
            }
            return bmOut;
        }
        return bm;
    }
    public Bitmap BITMAP_RESIZER(Bitmap bitmap) {
        int newWidth = bitmap.getWidth();
        int newHeight = bitmap.getHeight();
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
    @Override
    public void onBackPressed() {
        Intent intentUpdate = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentUpdate);
    }
}
