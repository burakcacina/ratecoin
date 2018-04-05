package com.example.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burak.ratecoindeneme.models.IssueModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
 * Created by burak on 16.02.2018.
 */
public class DetailsActivity extends AppCompatActivity {

    int rateID,rateUserID,status,USERID,i=0,selected;
    int[] optionsID = new int[4];
    String mail,description,desc_image,result,issueuser_image,issueuser_name;
    String[] opt = new String[4];
    Bitmap[] bitmaps = new Bitmap[3];
    Bitmap[] rotatedBitmaps = new Bitmap[3];
    ImageView issueSecondimage, issueFirstimage;
    TextView issueDescription,option_1,option_2,option_3,option_4,optresult_1,optresult_2,optresult_3,optresult_4;
    Button issueVoteopt;
    boolean check = false;
    private ProgressDialog dialog;
    String URL_TO_HIT;
    IssueModel recipeModel = new IssueModel();

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         USERID = prefs.getInt("USERID",-1);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String json = bundle.getString("issueModel");
            recipeModel = new Gson().fromJson(json, IssueModel.class);

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



        URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/issue_vote.php?voteid";

        if(status == 0) {
            if (URLUtil.isValidUrl(opt[0]) && URLUtil.isValidUrl(opt[1])) {
                Intent intent = new Intent(getApplicationContext(),IssueResultActivity.class);
                intent.putExtra("issueModel", new Gson().toJson(recipeModel));
                intent.putExtra("result", 1);  // pass your values and retrieve them in the other Activity using keyName
                startActivity(intent);

            }
            else if(URLUtil.isValidUrl(desc_image))
            {
                Intent intent = new Intent(getApplicationContext(),IssueResultActivity.class);
                intent.putExtra("issueModel", new Gson().toJson(recipeModel));
                intent.putExtra("result", 2);  // pass your values and retrieve them in the other Activity using keyName
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(),IssueResultActivity.class);
                intent.putExtra("issueModel", new Gson().toJson(recipeModel));
                intent.putExtra("result", 3);  // pass your values and retrieve them in the other Activity using keyName
                startActivity(intent);
            }
        }
        else {
            if (URLUtil.isValidUrl(opt[0]) && URLUtil.isValidUrl(opt[1])) {
                new DownloadImageTask().execute();
            }
            else if(URLUtil.isValidUrl(desc_image))
            {
                new DownloadOneImageTask().execute();
            }
            else
            {
                new GetOptionsTask().execute();
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class GetOptionsTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            setContentView(R.layout.activity_issuewithtext);
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
            issueVoteopt = (Button) findViewById(R.id.issueVoteOptions);
            issueVoteopt.setVisibility(View.GONE);

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


            return null;
        }

        protected void onPostExecute(String result2) {
            super.onPostExecute(result2);

            option_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(true);
                    option_2.setSelected(false);
                    option_3.setSelected(false);
                    option_4.setSelected(false);
                    selected=1;
                }
            });
            option_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(false);
                    option_2.setSelected(true);
                    option_3.setSelected(false);
                    option_4.setSelected(false);
                    selected=2;

                }
            });
            option_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(false);
                    option_2.setSelected(false);
                    option_3.setSelected(true);
                    option_4.setSelected(false);
                    selected=3;

                }
            });
            option_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(false);
                    option_2.setSelected(false);
                    option_3.setSelected(false);
                    option_4.setSelected(true);
                    selected=4;
                }
            });
            issueVoteopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new JSONTask().execute(String.valueOf(selected));
                }
            });
            issueVoteopt.setVisibility(View.VISIBLE);

        }
    }

    private class DownloadOneImageTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            setContentView(R.layout.activity_issuewithoneimages);

        }
        protected String doInBackground(String... urls) {

            issueVoteopt = (Button) findViewById(R.id.issueVoteOptions);
            issueVoteopt.setVisibility(View.GONE);
            issueFirstimage = (ImageView) findViewById(R.id.issueFirstimage);
            issueDescription = (TextView) findViewById(R.id.IssueDescription);
            option_1 = (TextView) findViewById(R.id.options_1);
            option_2 = (TextView) findViewById(R.id.options_2);
            option_3 = (TextView) findViewById(R.id.options_3);
            option_4 = (TextView) findViewById(R.id.options_4);

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
                    System.out.println(width + " " + height);
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
                    check = true;
                    issueFirstimage.setSelected(true);
                }
            });

            issueVoteopt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new JSONTask().execute(URL_TO_HIT);
                }
            });

            option_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(true);
                    option_2.setSelected(false);
                    option_3.setSelected(false);
                    option_4.setSelected(false);
                    selected=1;
                }
            });
            option_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(false);
                    option_2.setSelected(true);
                    option_3.setSelected(false);
                    option_4.setSelected(false);
                    selected=2;

                }
            });
            option_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(false);
                    option_2.setSelected(false);
                    option_3.setSelected(true);
                    option_4.setSelected(false);
                    selected=3;

                }
            });
            option_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    option_1.setSelected(false);
                    option_2.setSelected(false);
                    option_3.setSelected(false);
                    option_4.setSelected(true);
                    selected=4;
                }
            });


            issueVoteopt.setVisibility(View.VISIBLE);
            issueDescription.setText(description);
            issueFirstimage.setImageBitmap(rotatedBitmaps[0]);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            setContentView(R.layout.activity_issuewithimages);

        }
        protected Bitmap doInBackground(String... urls) {

            issueFirstimage = (ImageView) findViewById(R.id.issueFirstimage);
            issueSecondimage = (ImageView) findViewById(R.id.issueSecondimage);
            issueDescription = (TextView) findViewById(R.id.IssueDescription);
            issueVoteopt = (Button) findViewById(R.id.issueVoteOptions);
            issueVoteopt.setVisibility(View.GONE);

            try {
                InputStream in = new java.net.URL(opt[0]).openStream();
                bitmaps[0] = BitmapFactory.decodeStream(in);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;


                InputStream in2 = new java.net.URL(opt[1]).openStream();
                bitmaps[1] = BitmapFactory.decodeStream(in2,null,options);

                for(int i=0; i<2; i++) {

                    int width = bitmaps[i].getWidth();
                    int height = bitmaps[i].getHeight();

                    if (width > height) {
                        rotatedBitmaps[i] = BITMAP_RESIZER(bitmaps[i]);
                    }
                    else if(width == height)
                    {
                        rotatedBitmaps[i] = rotate(bitmaps[i], 90);
                    }
                    else {
                        rotatedBitmaps[i] = BITMAP_RESIZER(bitmaps[i]);
                    }
                    System.out.println(width + " " + height);
                }


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            dialog.cancel();

            issueFirstimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check = true;
                    issueFirstimage.setSelected(true);
                    issueSecondimage.setSelected(false);


                }
            });

            issueSecondimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check = false;
                    issueFirstimage.setSelected(false);
                    issueSecondimage.setSelected(true);

                }
            });

            issueVoteopt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new JSONTask().execute(URL_TO_HIT);
                }
            });

            issueVoteopt.setVisibility(View.VISIBLE);
            issueDescription.setText(description);
            issueFirstimage.setImageBitmap(rotatedBitmaps[0]);
            issueSecondimage.setImageBitmap(rotatedBitmaps[1]);
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
                final JSONObject jsonParam = new JSONObject();
                jsonParam.put("userID", USERID);
                jsonParam.put("rateID", rateID);

                if(params[0] == String.valueOf(selected))
                {
                    if (params[0].contains("1")) {
                        jsonParam.put("optID", optionsID[0]);
                    }
                    else if(params[0].contains("2"))
                    {
                        jsonParam.put("optID", optionsID[1]);
                    }
                    else if(params[0].contains("3"))
                    {
                        jsonParam.put("optID", optionsID[2]);
                    }
                    else {
                        jsonParam.put("optID", optionsID[3]);
                    }
                }
                else {
                    if (check) {
                        jsonParam.put("optID", optionsID[0]);

                    } else {
                        jsonParam.put("optID", optionsID[1]);
                    }
                }

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
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    result = jsonObj.getString("message");
                    System.out.println(result);
                }
                else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    JSONObject jsonObj = new JSONObject(sb.toString());
                    result = jsonObj.getString("message");
                    System.out.println(result);
                }
                return result;

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
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();




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