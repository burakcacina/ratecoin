package com.example.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burak.ratecoindeneme.models.IssueModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by burak on 24.03.2018.
 */

public class IssueResultActivity extends AppCompatActivity {

    int rateID,rateUserID,status,USERID,resultID,i=0;
    int[] optionsID = new int[4];
    String mail,description,desc_image,issueuser_name,issueuser_image;
    String[] opt = new String[4];
    String[] result = new String[5];
    Bitmap[] bitmaps = new Bitmap[3];
    Bitmap[] rotatedBitmaps = new Bitmap[3];
    ImageView issueSecondimage, issueFirstimage,user_imageissue;
    TextView issueDescription,option_1,option_2,option_3,option_4,optresult_1,optresult_2,optresult_3,optresult_4,issueOwnerName;
    private ProgressDialog dialog;
    String URL_TO_HIT;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            resultID = bundle.getInt("result");
            String json = bundle.getString("issueModel");
            IssueModel recipeModel = new Gson().fromJson(json, IssueModel.class);
            rateID = recipeModel.getRateID();
            rateUserID = recipeModel.getid();
            mail = recipeModel.getMail();
            status = recipeModel.getStatus();
            description = recipeModel.getDescription();
            desc_image = recipeModel.getDesc_image();
            issueuser_image=recipeModel.getImage();
            issueuser_name=recipeModel.getName();
            for (IssueModel.created created1 : recipeModel.getcreatedList()) {
                opt[i] = created1.getOptions();
                optionsID[i] = created1.getoptionsID();
                i++;
            }
        }
            URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/issue_result.php?rateID=" + rateID;

        if(resultID == 1)
        {
            new DownloadImageTask().execute();
        }
        else if(resultID == 2)
        {
            new DownloadOneImageTask().execute();
        }
        else
        {
            new GetOptionsTask().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetOptionsTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            setContentView(R.layout.activity_resultissuewithtext);
            new JSONTask().execute();
        }
        protected String doInBackground(String... urls) {

            option_1 = (TextView) findViewById(R.id.options_1);
            option_2 = (TextView) findViewById(R.id.options_2);
            option_3 = (TextView) findViewById(R.id.options_3);
            option_4 = (TextView) findViewById(R.id.options_4);
            optresult_1 = (TextView) findViewById(R.id.optresult_1);
            optresult_2 = (TextView) findViewById(R.id.optresult_2);
            optresult_3 = (TextView) findViewById(R.id.optresult_3);
            optresult_4 = (TextView) findViewById(R.id.optresult_4);
            issueDescription = (TextView) findViewById(R.id.IssueDescription);
            return null;
        }

        protected void onPostExecute(String result2) {
            super.onPostExecute(result2);

            if(opt[3] == null) {
                option_1.setText(opt[0]);
                option_2.setText(opt[1]);
                if(opt[2] == null)
                {
                    option_3.setVisibility(View.GONE);
                }
                else
                {
                    option_3.setText(opt[2]);
                }
                option_4.setVisibility(View.GONE);
            }

            else{
                option_1.setText(opt[0]);
                option_2.setText(opt[1]);
                option_3.setText(opt[2]);
                option_4.setText(opt[3]);
            }
            issueDescription.setText(description);
            optresult_1.setText(result[0]);
            optresult_2.setText(result[1]);
            optresult_3.setText(result[2]);
            optresult_4.setText(result[3]);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadOneImageTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            setContentView(R.layout.activity_resultissuewithoneimages);
            new JSONTask().execute();

        }
        protected String doInBackground(String... urls) {

            issueFirstimage = (ImageView) findViewById(R.id.issueFirstimage);
            issueDescription = (TextView) findViewById(R.id.IssueDescription);
            option_1 = (TextView) findViewById(R.id.options_1);
            option_2 = (TextView) findViewById(R.id.options_2);
            option_3 = (TextView) findViewById(R.id.options_3);
            option_4 = (TextView) findViewById(R.id.options_4);
            optresult_1 = (TextView) findViewById(R.id.optresult_1);
            optresult_2 = (TextView) findViewById(R.id.optresult_2);
            optresult_3 = (TextView) findViewById(R.id.optresult_3);
            optresult_4 = (TextView) findViewById(R.id.optresult_4);

            try {
                InputStream in = new java.net.URL(desc_image).openStream();
                bitmaps[0] = BitmapFactory.decodeStream(in);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                int width = bitmaps[0].getWidth();
                int height = bitmaps[0].getHeight();

                if (width > height) {
                    rotatedBitmaps[0] = BITMAP_RESIZER(bitmaps[0]);
                } else if (width == height) {
                    rotatedBitmaps[0] = rotate(bitmaps[0], 90);

                } else {
                    rotatedBitmaps[0] = BITMAP_RESIZER(bitmaps[0]);
                }


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result2) {
            super.onPostExecute(result2);
            dialog.cancel();

            if(opt[3] == null) {
                option_1.setText(opt[0]);
                option_2.setText(opt[1]);
                if(opt[2] == null)
                {
                    option_3.setVisibility(View.GONE);
                }
                else
                {
                    option_3.setText(opt[2]);
                }
                option_4.setVisibility(View.GONE);
            }

            else{
                option_1.setText(opt[0]);
                option_2.setText(opt[1]);
                option_3.setText(opt[2]);
                option_4.setText(opt[3]);
            }

            issueFirstimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    issueFirstimage.setSelected(true);
                }
            });

            issueDescription.setText(description);
            issueFirstimage.setImageBitmap(rotatedBitmaps[0]);
            optresult_1.setText(result[0]);
            optresult_2.setText(result[1]);
            optresult_3.setText(result[2]);
            optresult_4.setText(result[3]);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            super.onPreExecute();
            setContentView(R.layout.activity_resultissuewithimages);
            dialog.show();
            new JSONTask().execute();

        }
        protected Bitmap doInBackground(String... urls) {

            issueFirstimage = (ImageView) findViewById(R.id.issueFirstimage);
            issueSecondimage = (ImageView) findViewById(R.id.issueSecondimage);
            user_imageissue = (ImageView) findViewById(R.id.user_image);
            optresult_1 = (TextView) findViewById(R.id.optresult_1);
            optresult_2 = (TextView) findViewById(R.id.optresult_2);
            issueDescription = (TextView) findViewById(R.id.IssueDescription);
            issueOwnerName = (TextView) findViewById(R.id.issueOwnerName);

            try {

                for(int i=0; i<2; i++) {

                    InputStream in = new java.net.URL(opt[i]).openStream();
                    bitmaps[i] = BitmapFactory.decodeStream(in);

                    int width = bitmaps[i].getWidth();
                    int height = bitmaps[i].getHeight();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;

                    if (width > height) {
                        rotatedBitmaps[i] = BITMAP_RESIZER(bitmaps[i]);
                    } else if (width == height) {
                        rotatedBitmaps[i] = rotate(bitmaps[i], 90);

                    } else {
                        rotatedBitmaps[i] = BITMAP_RESIZER(bitmaps[i]);
                    }

                }
                InputStream in = new java.net.URL(issueuser_image).openStream();
                bitmaps[2] = BitmapFactory.decodeStream(in);
                rotatedBitmaps[2] = BITMAP_RESIZER(bitmaps[2]);


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Bitmap a) {
            super.onPostExecute(a);
            dialog.cancel();
            issueDescription.setText(description);
            issueFirstimage.setImageBitmap(rotatedBitmaps[0]);
            issueSecondimage.setImageBitmap(rotatedBitmaps[1]);
            user_imageissue.setImageBitmap(rotatedBitmaps[2]);
            optresult_1.setText(result[0]);
            optresult_2.setText(result[1]);
            issueOwnerName.setText(issueuser_name);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(URL_TO_HIT);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect(); //Create JSONObject here JSONObject

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
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject finalObject = jsonArray.getJSONObject(j);
                        result[j] = finalObject.getString("result");
                        System.out.println(result[j]);
                    }

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

        protected void onPostExecute(String[] result) {
            super.onPostExecute(result[0]);





        }
    }

    private Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Bitmap bmOut;
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            if (bm.getWidth() >= 3024 && bm.getHeight() >= 3024)
            {
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
        int newWidth = issueFirstimage.getWidth()-20;
        int newHeight = issueFirstimage.getHeight()-20;
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
