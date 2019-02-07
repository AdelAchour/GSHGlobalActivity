package com.production.achour_ar.gshglobalactivity.ITs.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.ITs.activity.StatsTickets;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.Constants;
import com.production.achour_ar.gshglobalactivity.ITs.fragment.ListTickets;
import com.production.achour_ar.gshglobalactivity.R;

import java.util.ArrayList;

public class DialogChoixPersonStat {
    Activity mActivity;
    EditText txtPrenom, txtEmail;

    public void showDialog(final Activity activity){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_choix_person_stat);

        txtPrenom = dialog.findViewById(R.id.ETName);
        txtEmail = dialog.findViewById(R.id.ETemail);

        Button FermerDialog = dialog.findViewById(R.id.fermer);
        Button SendBtn = dialog.findViewById(R.id.envoyerEmailBtn);

        FermerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prenomR = txtPrenom.getText().toString();
                String emailR = txtEmail.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("prenomR",prenomR);
                bundle.putString("emailR",emailR);

                Message msg = new Message();
                msg.what = Constants.SEND_EMAIL_PERSON;
                msg.setData(bundle);
                StatsTickets.handler.sendMessage(msg); //Save motif
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
