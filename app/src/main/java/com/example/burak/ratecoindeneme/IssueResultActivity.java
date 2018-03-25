package com.example.burak.ratecoindeneme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by burak on 24.03.2018.
 */

public class IssueResultActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issueresult);
        A();
    }

    public Integer A() {
        int a;
        if (5 > 2) {
            a = 1;
        } else
            a = 5;
        System.out.println(a + "sa");
        return a;

    }

    @Override
    public void onBackPressed() {

        Intent intentUpdate = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intentUpdate);
    }
}
