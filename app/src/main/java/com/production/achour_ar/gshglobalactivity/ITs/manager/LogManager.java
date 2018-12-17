package com.production.achour_ar.gshglobalactivity.ITs.manager;

import android.util.Log;

public class LogManager {

    public static void print(){
        Log.d("MyTAG"," HEY I'M HERE!");
    }

    public static void print(String message){
        Log.d("MyTAG", message);
    }

    public static void print(String tag, String message){
        Log.d(tag, message);
    }

}
