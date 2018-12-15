package com.production.achour_ar.gshglobalactivity.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.data_model.Constants;

public class AboutActivity extends AppCompatActivity {

    private TextView VersionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        initView();
        setupActionBar();
        setupTVs();

    }

    private void setupTVs() {
        String version = "Version "+ Constants.APP_VERSION;
        VersionTV.setText(version);
    }

    private void initView() {
        VersionTV = findViewById(R.id.versionstring);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //show a caret even if android:parentActivityName is not specified.
        actionBar.setTitle("A propos et FAQ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
