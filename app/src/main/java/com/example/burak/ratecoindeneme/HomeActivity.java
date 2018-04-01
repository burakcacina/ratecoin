package com.example.burak.ratecoindeneme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.burak.ratecoindeneme.models.IssueModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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
public class HomeActivity extends AppCompatActivity {
    private ListView lvIssues;
    private ProgressDialog dialog;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        lvIssues = (ListView)findViewById(R.id.lvIssues);

        String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/issue_details.php";
        new JSONTask().execute(URL_TO_HIT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int USERID = prefs.getInt("USERID",-1);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateIssueActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateIssuewithOneImageActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateIssuewithImageActivity.class);
                startActivity(intent);
            }
        });

    }

    public class JSONTask extends AsyncTask<String,String, List<IssueModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<IssueModel> doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect(); //Create JSONObject here JSONObject

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                List<IssueModel> IssueModelList = new ArrayList<>();

                JSONObject jsonObj = new JSONObject(sb.toString());
                JSONArray parentArray = jsonObj.getJSONArray("issues");
                Gson gson = new Gson();

                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    IssueModel IssueModel = gson.fromJson(finalObject.toString(), IssueModel.class);
                    List<IssueModel.created> createdList = new ArrayList<>();

                    for (int j = 0; j < finalObject.getJSONArray("created").length(); j++) {
                        IssueModel.created created = new IssueModel.created();
                        created.setoptionsID(finalObject.getJSONArray("created").getJSONObject(j).getInt("optionsID"));
                        created.setOptions(finalObject.getJSONArray("created").getJSONObject(j).getString("options_1"));
                        createdList.add(created);
                    }
                    IssueModel.setCreatedList(createdList);
                    IssueModelList.add(IssueModel);
                }
                return IssueModelList;

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
        protected void onPostExecute(final List<IssueModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                IssueAdapter adapter = new IssueAdapter(getApplicationContext(), R.layout.activity_showissue, result);
                lvIssues.setAdapter(adapter);
                lvIssues.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        IssueModel IssueModel = result.get(position);
                        Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                        intent.putExtra("issueModel", new Gson().toJson(IssueModel));
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, no internet connection found.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class IssueAdapter extends ArrayAdapter {

        private List<IssueModel> IssueModelList;
        private int resource;
        private LayoutInflater inflater;
        public IssueAdapter(Context context, int resource, List<IssueModel> objects) {
            super(context, resource, objects);
            IssueModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivIssueIcon = (ImageView)convertView.findViewById(R.id.profile_image);
                holder.tvIssueName = (TextView)convertView.findViewById(R.id.tvIssueName);
                holder.tvIssueID = (TextView)convertView.findViewById(R.id.tvIssueID) ;
                holder.frma = (FrameLayout) convertView.findViewById(R.id.frma);
                holder.tvCreated = (TextView)convertView.findViewById(R.id.tvCreated);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

            final ViewHolder finalHolder = holder;
            ImageLoader.getInstance().displayImage(IssueModelList.get(position).getImage(), holder.ivIssueIcon, new ImageLoadingListener() {

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
                    Matrix matrix = new Matrix();
                    System.out.println(loadedImage.getWidth() + " " + loadedImage.getHeight());
                    if(loadedImage.getWidth() == 378*2) {
                        matrix.postRotate(90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
                        finalHolder.ivIssueIcon.setImageBitmap(rotatedBitmap);
                    }
                    else if(loadedImage.getWidth() >= 648 && loadedImage.getHeight() >= 486)
                    {
                        matrix.postRotate(-90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
                        finalHolder.ivIssueIcon.setImageBitmap(rotatedBitmap);
                    }
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });


            holder.tvIssueName.setText(IssueModelList.get(position).getDescription());
            holder.tvIssueID.setText("ID: " + IssueModelList.get(position).getRateID());
            holder.tvCreated.setText("Created: " + IssueModelList.get(position).getName());
            if(IssueModelList.get(position).getStatus() == 1)
            {
                holder.frma.setBackgroundResource(R.color.Green);

            }
            else
            {
                holder.frma.setBackgroundResource(R.color.Red);
            }

            return convertView;
        }
        class ViewHolder{
            private ImageView ivIssueIcon;
            private ImageView ivRecipeIcon2;
            private TextView tvIssueName;
            private TextView tvIssueID;
            private TextView tvCreated;
            private FrameLayout frma;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_showuser) {
            Intent intentUpdate = new Intent(getApplicationContext(),UpdateAccActivity.class);
            startActivity(intentUpdate);
        }
        else if (item.getItemId() == R.id.action_showinfo) {
            Intent intentUpdate = new Intent(getApplicationContext(),UserDetailsActivity.class);
            startActivity(intentUpdate);
        }
        else if (item.getItemId() == R.id.action_exit) {
            this.finishAffinity();
        }
        return true;
    }
}