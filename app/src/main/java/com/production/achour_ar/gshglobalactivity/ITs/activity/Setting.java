package com.production.achour_ar.gshglobalactivity.ITs.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.production.achour_ar.gshglobalactivity.R;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ticketLinear, projectLinear, interventionLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        initView();
        setupActionBar();
        setListener();

    }

    private void setListener() {
        ticketLinear.setOnClickListener(this);
        //projectLinear.setOnClickListener(this);
        //interventionLinear.setOnClickListener(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Paramètres");
        actionBar.setHomeButtonEnabled(true);
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        ticketLinear = findViewById(R.id.ticketlinear);
        //projectLinear = findViewById(R.id.projectlinear);
        //interventionLinear = findViewById(R.id.interventionlinear);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ticketlinear:
                startActivity(new Intent(this, SettingTicket.class));
                break;

            /*case R.id.projectlinear:
                Toast.makeText(this, "En cours d'implémentation", Toast.LENGTH_SHORT).show();
                break;

            case R.id.interventionlinear:
                Toast.makeText(this, "En cours d'implémentation", Toast.LENGTH_SHORT).show();
                break;*/

        }
    }
}
