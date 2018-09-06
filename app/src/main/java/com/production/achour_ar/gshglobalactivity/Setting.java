package com.production.achour_ar.gshglobalactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    EditText rangeTicketET;
    Button btnSave;

    SharedPreferences app_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        rangeTicketET = (EditText)findViewById(R.id.rangeticketmax);
        btnSave = (Button) findViewById(R.id.buttonSaveSetting);

        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get the value for the run counter
        int range = app_preferences.getInt("range", 30);

        rangeTicketET.setText(String.valueOf(range));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String range = rangeTicketET.getText().toString().trim();
                if (Integer.valueOf(range)>=2000){
                    rangeTicketET.setError("Veuillez choisir un nombre inférieur à 2000. On augmentera le nombre dans de prochaines versions");
                    rangeTicketET.requestFocus();
                }
                else{
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putInt("range", Integer.valueOf(range));
                    editor.commit(); // Very important
                    Toast.makeText(getApplicationContext(),"Changements effectués", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
