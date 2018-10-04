package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    EditText rangeTicketET, timeActualisationET;
    Button btnSave;

    SharedPreferences app_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Paramètres");
        actionBar.setHomeButtonEnabled(true);

        rangeTicketET = (EditText)findViewById(R.id.rangeticketmax);
        timeActualisationET = (EditText)findViewById(R.id.timeactualisation);
        btnSave = (Button) findViewById(R.id.buttonSaveSetting);

        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get the value for the run counter
        int range = app_preferences.getInt("range", 30);
        final int timeLoad = app_preferences.getInt("load", 3); // 3 minutes

        rangeTicketET.setText(String.valueOf(range));
        timeActualisationET.setText(String.valueOf(timeLoad));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String range = rangeTicketET.getText().toString().trim();
                String load = timeActualisationET.getText().toString().trim();
                if (Integer.valueOf(range)>=2000){
                    rangeTicketET.setError("Veuillez choisir un nombre inférieur à 2000. On augmentera le nombre dans de prochaines versions");
                    rangeTicketET.requestFocus();
                }
                else if ((Integer.valueOf(load)<3)||(Integer.valueOf(load)>7)){
                    timeActualisationET.setError("L'intervalle doit être compris entre 3 et 7 (minutes)");
                    timeActualisationET.requestFocus();
                }
                else{
                    //save range
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("range", Integer.valueOf(range));
                    editor.commit(); // Very important

                    //save load time
                    editor.putInt("load", Integer.valueOf(load));
                    editor.commit(); // Very important


                    Toast.makeText(getApplicationContext(),"Changements effectués", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
