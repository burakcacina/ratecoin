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
import com.example.burak.ratecoindeneme.models.TransactionModel;
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
public class WalletActivity extends AppCompatActivity {
    private ListView lvTransaction;
    private ProgressDialog dialog;
    private TextView tvwalletPK;
    private TextView tvtotalAmount;
    String primaryKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        lvTransaction = (ListView)findViewById(R.id.lvTransaction);

        tvwalletPK = (TextView) findViewById(R.id.tvwalletPK);
        tvtotalAmount = (TextView) findViewById(R.id.tvtotalAmount);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int USERID = prefs.getInt("USERID",-1);

        String URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/accout_walletactivity.php?userID="+ USERID;
        new JSONTask().execute(URL_TO_HIT);

    }

    public class JSONTask extends AsyncTask<String, String, List<TransactionModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<TransactionModel> doInBackground(String... params) {
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

                    List<TransactionModel> TransactionModelList = new ArrayList<>();

                    JSONObject jsonObj = new JSONObject(sb.toString());
                    JSONArray parentArray = jsonObj.getJSONArray("transaction");
                    Gson gson = new Gson();

                    for(int i=0; i<parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        TransactionModel TransactionModel = gson.fromJson(finalObject.toString(), TransactionModel.class);
                        TransactionModelList.add(TransactionModel);
                    }
                    primaryKey = jsonObj.getString("PrimaryKey");
                    return TransactionModelList;
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
        protected void onPostExecute(final List<TransactionModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                TransactionAdapter adapter = new TransactionAdapter(getApplicationContext(), R.layout.activity_showtransaction, result);
                lvTransaction.setAdapter(adapter);
                tvtotalAmount.setText("Total Cost: " + result.get(0).getTotalcost());
                tvwalletPK.setText("Wallet Primary Key: "+ primaryKey);
            } else {
                Toast.makeText(getApplicationContext(), "Create Wallet First!", Toast.LENGTH_SHORT).show();
                Intent intentUpdate = new Intent(getApplicationContext(), CreateUserWalletActivity.class);
                startActivity(intentUpdate);
            }
        }
    }
    public class TransactionAdapter extends ArrayAdapter {

        private List<TransactionModel> TransactionModelList;
        private int resource;
        private LayoutInflater inflater;

        public TransactionAdapter(Context context, int resource, List<TransactionModel> objects) {
            super(context, resource, objects);
            TransactionModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.txSender = (TextView)convertView.findViewById(R.id.txSender);
                holder.txReceiver = (TextView)convertView.findViewById(R.id.txReceiver);
                holder.txRateID = (TextView)convertView.findViewById(R.id.txRateID) ;
                holder.txAmount = (TextView)convertView.findViewById(R.id.txAmount);
                holder.txNonce = (TextView)convertView.findViewById(R.id.txNonce);
                holder.txIndex = (TextView)convertView.findViewById(R.id.txIndex);
                holder.txHash = (TextView)convertView.findViewById(R.id.txHash);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.txReceiver.setText("Receiver: " +TransactionModelList.get(position).getReceiver());
            holder.txSender.setText("Sender: " +TransactionModelList.get(position).getSender());

            if (TransactionModelList.get(position).getrateID() == 0 && TransactionModelList.get(position).getBrand() == null)
            {
                holder.txRateID.setText("This is test Transaction!");
            }
            else if( TransactionModelList.get(position).getrateID() == 0)
            {
                holder.txRateID.setText("You bought product of:" + TransactionModelList.get(position).getBrand());
            }
            else
            {
                holder.txRateID.setText("You guessed! issue id:" + TransactionModelList.get(position).getrateID());
            }
            if( TransactionModelList.get(position).getSender().equals(primaryKey))
            {
                holder.txSender.setText("Sender: You!");
            }
            if( TransactionModelList.get(position).getReceiver().equals(primaryKey))
            {
                holder.txReceiver.setText("Receiver: You!");
            }


            holder.txAmount.setText("Amount: " +TransactionModelList.get(position).getAmount());
            holder.txNonce.setText("Nonce: " + TransactionModelList.get(position).getNonce());
            holder.txIndex.setText("Number of block: " + TransactionModelList.get(position).getTx_index());
            holder.txHash.setText("Hash : " + TransactionModelList.get(position).getHash());

            System.out.println(TransactionModelList.get(position).getrateID());

            return convertView;
        }
        class ViewHolder{
            private TextView txSender;
            private TextView txReceiver;
            private TextView txRateID;
            private TextView txAmount;
            private TextView txNonce;
            private TextView txIndex;
            private TextView txHash;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intentUpdate = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentUpdate);
        this.finish();

    }
}