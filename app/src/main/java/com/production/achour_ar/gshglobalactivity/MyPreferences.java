package com.production.achour_ar.gshglobalactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferences {

    private static SharedPreferences app_preferences;


    public static void SaveString(String key, String value){
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(key, value);
        editor.commit(); // Very important
    }

    public static void SaveInt(String key, int value){
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt(key, value);
        editor.commit(); // Very important
    }

    public static void SaveLong(String key, long value){
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putLong(key, value);
        editor.commit(); // Very important
    }

    public static String getMyString(Context context, String key, String defValue){
        app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Get the value for the run counter
        String value = app_preferences.getString(key, defValue);
        return value;
    }

    public static int getMyInt(Context context, String key, int defValue){
        app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Get the value for the run counter
        int value = app_preferences.getInt(key, defValue);
        return value;
    }

    public static long getMyLong(Context context, String key, long defValue){
        app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Get the value for the run counter
        long value = app_preferences.getLong(key, defValue);
        return value;
    }



}
