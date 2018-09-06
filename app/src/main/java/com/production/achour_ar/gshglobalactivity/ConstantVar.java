package com.production.achour_ar.gshglobalactivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ConstantVar extends Activity {

    static int RANGE_TICKET = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.info_ticket);

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("range", RANGE_TICKET);
        editor.commit(); // Very important


    }


}
