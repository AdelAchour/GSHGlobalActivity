package com.production.achour_ar.gshglobalactivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DialogTemps {
    Activity mActivity;

    public void showDialog(Activity activity, String statutTicket, String tempsResolution, String tempsRetard, boolean retard){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_temps);

        TextView StatutTV = (TextView) dialog.findViewById(R.id.StatutTicket);
        if (retard){
            StatutTV.setTextColor(Color.parseColor("#98272b"));
        }
        else {
            StatutTV.setTextColor(Color.parseColor("#1a791f"));
        }
        StatutTV.setText(statutTicket);

        TextView TempsResolutionTV = (TextView) dialog.findViewById(R.id.tempsresolution);
        TempsResolutionTV.setText("Temps de résolution : "+TransformInTime(tempsResolution));

        TextView TempsRetardTV = (TextView) dialog.findViewById(R.id.tempsretard);
        if (retard){
            TempsRetardTV.setText("Temps de retard : "+TransformInTime(tempsRetard));
        }
        else{
            TempsRetardTV.setText("Aucun retard");
        }



        Button FermerDialog = (Button) dialog.findViewById(R.id.fermer);
        FermerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private String TransformInTime(String tempsMS) {
        long timeLeftMS = Long.valueOf(tempsMS);
        //int months = (int) ((timeLeftMS / (30*24*60*60*1000)));
        int day = (int) ((timeLeftMS / (24*60*60*1000))); //%30
        int hour = (int) ((timeLeftMS / (1000*60*60)) % 24);
        int minute = (int) ((timeLeftMS / (60*1000)) % 60);
        int seconde = (int)timeLeftMS % 60000 / 1000;

        String timeLeftText = "";

        //if (months<10) timeLeftText += "0";
        //timeLeftText += months;
        //timeLeftText += ":";
        if (day<10) timeLeftText += "0";
        timeLeftText += day;
        timeLeftText += ":";
        if (hour<10) timeLeftText += "0";
        timeLeftText += hour;
        timeLeftText += ":";
        if (minute<10) timeLeftText += "0";
        timeLeftText += minute;
        timeLeftText += ":";
        if (seconde<10) timeLeftText += "0";
        timeLeftText += seconde;



        return timeLeftText;
    }

}
