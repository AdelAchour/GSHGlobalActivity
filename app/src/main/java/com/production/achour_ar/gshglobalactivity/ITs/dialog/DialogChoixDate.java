package com.production.achour_ar.gshglobalactivity.ITs.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.ITs.activity.StatsTickets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogChoixDate {
    Activity mActivity;
    String DateDebut, DateFin;
    String DateDebutRequest, DateFinRequest;
    private DatePickerDialog.OnDateSetListener mDateSetListenerDebut, mDateSetListenerFin;

    public void showDialog(Activity activity, final String datedebut, final String datefin){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_choix_date);

        final TextView datedebutTV = (TextView) dialog.findViewById(R.id.datedebut);
        datedebutTV.setText(TransformDate(datedebut));

        final TextView datefinTV = (TextView) dialog.findViewById(R.id.datefin);
        datefinTV.setText(TransformDate(datefin));

        datedebutTV.setPaintFlags(datedebutTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        datefinTV.setPaintFlags(datefinTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        datedebutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yyyy-MM-dd HH:mm:ss
                int year = getYearFromDate(datedebut);
                int month = getMonthFromDate(datedebut);
                int day = getDayFromDate(datedebut);

                DatePickerDialog dialog = new DatePickerDialog(
                        mActivity,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerDebut,
                        year,month-1,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenerDebut = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("Date", "onDateSet: mm/dd/yyy: " + day + "/" + month + "/" + year);

                String Day = "", Month = "";
                if (day<10) Day = "0"+day;
                else Day = ""+day;
                if (month<10) Month = "0"+month;
                else Month = ""+month;

                DateDebut = Day + "/" + Month + "/" + year;
                DateDebutRequest = year+"-"+Month+"-"+Day+" 00:00:00"; //yyyy-MM-dd HH:mm:ss
                Log.d("DateRequest", DateDebutRequest);
                datedebutTV.setText(DateDebut);
            }
        };



        datefinTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yyyy-MM-dd HH:mm:ss
                int year = getYearFromDate(datefin);
                int month = getMonthFromDate(datefin);
                int day = getDayFromDate(datefin);

                DatePickerDialog dialog = new DatePickerDialog(
                        mActivity,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerFin,
                        year,month-1,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenerFin = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("Date", "onDateSet: mm/dd/yyy: " + day + "/" + month + "/" + year);

                String Day = "", Month = "";
                if (day<10) Day = "0"+day;
                else Day = ""+day;
                if (month<10) Month = "0"+month;
                else Month = ""+month;

                DateFin = Day + "/" + Month + "/" + year;
                DateFinRequest = year+"-"+Month+"-"+Day+" 17:00:00"; //yyyy-MM-dd HH:mm:ss
                Log.d("DateRequest", DateFinRequest);
                datefinTV.setText(DateFin);
            }
        };


        Button recherche = (Button) dialog.findViewById(R.id.recherche);
        recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("debut", datedebutTV.getText().toString());
                bundle.putString("fin", datefinTV.getText().toString());

                String debutRequest = transformDateDebutRequest(datedebutTV.getText().toString());
                String finRequest = transformDateFinRequest(datefinTV.getText().toString());
                bundle.putString("debutRequest", debutRequest);
                bundle.putString("finRequest", finRequest);

                Message msg = new Message();
                msg.what = 0;
                msg.setData(bundle);

                StatsTickets.handlerDate.sendMessage(msg);
                dialog.dismiss();
            }
        });

        Button FermerFialog = (Button) dialog.findViewById(R.id.fermer);
        FermerFialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private String transformDateFinRequest(String s) {
        // dd/mm/yyyy to yyyy-MM-dd HH:mm:ss
        String day = s.substring(0,2);
        String month = s.substring(3,5);
        String year = s.substring(6);

        return year+"-"+month+"-"+day+" 17:00:00";
    }

    private String transformDateDebutRequest(String s) {
        // dd/mm/yyyy to yyyy-MM-dd HH:mm:ss
        String day = s.substring(0,2);
        String month = s.substring(3,5);
        String year = s.substring(6);

        return year+"-"+month+"-"+day+" 00:00:00";
    }

    private int getDayFromDate(String date) {
        //yyyy-MM-dd HH:mm:ss
        return Integer.valueOf(date.substring(8,10));
    }

    private int getMonthFromDate(String date) {
        //yyyy-MM-dd HH:mm:ss
        return Integer.valueOf(date.substring(5,7));
    }

    private int getYearFromDate(String date) {
        //yyyy-MM-dd HH:mm:ss
        return Integer.valueOf(date.substring(0,4));
    }

    private String TransformDate(String date) {
        //yyyy-MM-dd HH:mm:ss
        String dateWithoutTime = date.substring(0, 10);

        String newDate;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ddate = null;

        try {

            ddate = sdf.parse(dateWithoutTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf.applyPattern("dd/MM/yyyy");
        newDate = sdf.format(ddate);

        return newDate;
    }

}
