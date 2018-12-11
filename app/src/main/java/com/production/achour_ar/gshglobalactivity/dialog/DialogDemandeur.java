package com.production.achour_ar.gshglobalactivity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;

public class DialogDemandeur {

    Activity mActivity;

    public void showDialog(Activity activity, String NomPrenom, String Email, final String Tel, String Lieu, String Poste){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_demandeur);

        TextView NomPrenomTV = (TextView) dialog.findViewById(R.id.NomPrenom);
        NomPrenomTV.setText(NomPrenom);

        TextView EmailTV = (TextView) dialog.findViewById(R.id.emailasker);
        EmailTV.setText(Email);

        TextView TelTV = (TextView) dialog.findViewById(R.id.telasker);
        if (Tel.equals("")){
            TelTV.setText("Aucun numéro de Tél.");
        }
        else {
            if (Tel.length()==9){
                TelTV.setText(DisplayNumber(Tel));
            }
            else {
                TelTV.setText(Tel);
            }
        }



        TextView LieuTV = (TextView) dialog.findViewById(R.id.lieuasker);
        LieuTV.setText(Lieu);

        TextView PosteTV = (TextView) dialog.findViewById(R.id.posteAsker);
        PosteTV.setText(Poste);




        Button CallAsker = (Button) dialog.findViewById(R.id.callasker);
        CallAsker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "";
                if (Tel.length()==9){
                    number = "0"+Tel;
                }
                else{
                    number = Tel;
                }
                Uri call = Uri.parse("tel:" + number);
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                mActivity.startActivity(surf);

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

    private String DisplayNumber(String telNumber) {
        String number = "";
        number = "0"+telNumber; //0560 93 14 79

        String part1 = number.substring(0,4);
        String part2 = number.substring(4,6);
        String part3 = number.substring(6,8);
        String part4 = number.substring(8,10);

        number = part1 + " " + part2 + " " + part3 + " " + part4;


        return number;
    }
}