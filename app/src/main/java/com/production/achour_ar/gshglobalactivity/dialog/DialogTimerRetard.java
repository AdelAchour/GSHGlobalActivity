package com.production.achour_ar.gshglobalactivity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DialogTimerRetard {
    TextView TempsRetardTV;
    Activity mActivity;
    Chronometer chrono;

    long retard;
    long time;

    public void showDialog(Activity activity, final String dateEcheance, boolean retard){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_timer_retard);

        chrono  = (Chronometer)dialog.findViewById(R.id.livetempsretard);

        TempsRetardTV = (TextView) dialog.findViewById(R.id.tempsretard);

        if (retard){
            TempsRetardTV.setText("");
        }
        else{
            TempsRetardTV.setTextColor(Color.parseColor("#ff393939"));
            TempsRetardTV.setText("Pas de retard pour l'instant");
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

    private void StartTimer(String timeretard) {
        final long longtime = Long.valueOf(timeretard);
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = longtime - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s = (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.setText("00:00:00");
    }

    private String CalculTempsRetard(String dateEchanceTicket) {
        long echeance = getDateDebutMS(dateEchanceTicket);
        long now = System.currentTimeMillis();

        long tmps = now - echeance;
        return String.valueOf(tmps);
    }

    private long getDateDebutMS(String dateDebutTicket) {
        long dateDebutMS = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //"2018-07-17 11:58:47
        formatter.setLenient(false);

        String oldTime = dateDebutTicket;
        Date oldDate = null;
        try {
            oldDate = formatter.parse(oldTime);
        } catch (ParseException e) { e.printStackTrace(); }
        dateDebutMS = oldDate.getTime();

        return dateDebutMS;
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
