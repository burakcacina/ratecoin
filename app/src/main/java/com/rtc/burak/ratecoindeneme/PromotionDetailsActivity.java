package com.rtc.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rtc.burak.ratecoindeneme.models.IssueModel;
import com.rtc.burak.ratecoindeneme.models.PromotionModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by burak on 16.02.2018.
 */
public class PromotionDetailsActivity extends AppCompatActivity {

    int promoID,USERID,i=0,cost;
    String description,result,image,brand,code,URL_TO_HIT;
    ImageView Promo_image;
    TextView Promo_desc,Promo_brand,Promo_cost,Promo_code;
    private ProgressDialog dialog;
    PromotionModel promotionModel = new PromotionModel();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotionbuy);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        Promo_image = (ImageView) findViewById(R.id.Promo_image);
        Promo_brand = (TextView) findViewById(R.id.Promo_brand);
        Promo_desc = (TextView) findViewById(R.id.Promo_desc);
        Promo_cost = (TextView) findViewById(R.id.Promo_cost);
        Promo_code = (TextView) findViewById(R.id.Promo_code);

        Button but1 = (Button) findViewById(R.id.takeSS);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    takeScreenshot();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         USERID = prefs.getInt("USERID",-1);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String json = bundle.getString("promotionModel");
            promotionModel = new Gson().fromJson(json, PromotionModel.class);
            promoID = promotionModel.getid();
            brand = promotionModel.getBrand();
            description = promotionModel.getDescription();
            cost = promotionModel.getcost();
            image = promotionModel.getImage();
        }
        System.out.println(description +" " + brand +" " + cost + " " +image);
        URL_TO_HIT ="http://localapi25.atwebpages.com/android_connect/promotionBuy.php?userID="+USERID + "&promoID="+ promoID;
        System.out.println(URL_TO_HIT);
        new JSONTask().execute(URL_TO_HIT);
    }

    private void takeScreenshot() throws IOException {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String fileName = now + ".jpg";
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");
            folder.mkdirs();  //create directory

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(folder, fileName);
            imageFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(PromotionDetailsActivity.this, "ScreenShot Captured", Toast.LENGTH_SHORT).show();

            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
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
                httpURLConnection.connect();

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
                    code = jsonObj.getString("code");
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
            Toast.makeText(getApplicationContext(), "Before leave take Screenshot!", Toast.LENGTH_LONG).show();
            Promo_brand.setText("Brand: " +brand);
            Promo_desc.setText(description);
            Promo_cost.setText("Cost: " + cost + " RTC");
            Promo_code.setText("Promotion code: " +code);
        }
    }

    @Override
    public void onBackPressed() {
            Intent intentUpdate = new Intent(getApplicationContext(), PromotionsActivity.class);
            startActivity(intentUpdate);
            this.finish();
    }
}

