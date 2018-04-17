package com.example.burak.ratecoindeneme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burak.ratecoindeneme.models.PromotionModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by burak on 16.02.2018.
 */
public class PromotionsActivity extends AppCompatActivity {
    private ListView lvPromotions;
    private ProgressDialog dialog;
    private TextView tvtotalAmount;
    int totalcost;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);

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

        lvPromotions = (ListView)findViewById(R.id.lvPromotions);

        tvtotalAmount = (TextView) findViewById(R.id.tvtotalAmount);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int USERID = prefs.getInt("USERID",-1);

        String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/promotionList.php?userID=" + USERID;

        System.out.println(URL_TO_HIT);
        new JSONTask().execute(URL_TO_HIT);

    }

    public class JSONTask extends AsyncTask<String, String, List<PromotionModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<PromotionModel> doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect(); //Create JSONObject here JSONObject
                int HttpResult = httpURLConnection.getResponseCode();
                System.out.println(HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    List<PromotionModel> PromotionModelList = new ArrayList<>();

                    JSONObject jsonObj = new JSONObject(sb.toString());
                    JSONArray parentArray = jsonObj.getJSONArray("promotions");
                    Gson gson = new Gson();

                    for(int i=0; i<parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        PromotionModel PromotionModel = gson.fromJson(finalObject.toString(), PromotionModel.class);
                        PromotionModelList.add(PromotionModel);
                    }
                    totalcost = jsonObj.getInt("totalcost");
                    return PromotionModelList;
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
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
            return  null;
        }

        @Override
        protected void onPostExecute(final List<PromotionModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                PromotionAdapter adapter = new PromotionAdapter(getApplicationContext(), R.layout.activity_showpromotions, result);
                lvPromotions.setAdapter(adapter);
                tvtotalAmount.setText("Total Cost: " + totalcost +" RTC");
                lvPromotions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(PromotionsActivity.this);
                        builder.setTitle("Buy Promotion")
                                .setMessage("Are you sure you want to buy this promotion?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(result.get(position).getcost() > totalcost)
                                        {
                                            Toast.makeText(getApplicationContext(), "Not Enought RTC", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            PromotionModel PromotionModel = result.get(position);
                                            Intent intent = new Intent(PromotionsActivity.this, PromotionDetailsActivity.class);
                                            intent.putExtra("promotionModel", new Gson().toJson(PromotionModel));
                                            startActivity(intent);
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, no internet connection found.", Toast.LENGTH_SHORT).show();

            }
        }
    }
    public class PromotionAdapter extends ArrayAdapter {

        private List<PromotionModel> PromotionModelList;
        private int resource;
        private LayoutInflater inflater;
        public PromotionAdapter(Context context, int resource, List<PromotionModel> objects) {
            super(context, resource, objects);
            PromotionModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivPro_image = (ImageView)convertView.findViewById(R.id.ivPro_image);
                holder.tvPro_brand = (TextView)convertView.findViewById(R.id.tvPro_brand);
                holder.tvPro_description = (TextView)convertView.findViewById(R.id.tvPro_Description) ;
                holder.tvPro_cost = (TextView)convertView.findViewById(R.id.tvPro_cost);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }



            final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

            final ViewHolder finalHolder = holder;
            ImageLoader.getInstance().displayImage(PromotionModelList.get(position).getImage(), holder.ivPro_image, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivPro_image.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });


            holder.tvPro_description.setText(PromotionModelList.get(position).getDescription());
            holder.tvPro_cost.setText("Cost: " + PromotionModelList.get(position).getcost() + " RTC");
            holder.tvPro_brand.setText("Brand: " + PromotionModelList.get(position).getBrand());


            return convertView;
        }
        class ViewHolder{
            private ImageView ivPro_image;
            private TextView tvPro_brand;
            private TextView tvPro_description;
            private TextView tvPro_cost;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intentUpdate = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentUpdate);
        this.finish();
    }
}