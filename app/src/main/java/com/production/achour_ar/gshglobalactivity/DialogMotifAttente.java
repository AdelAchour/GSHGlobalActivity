package com.production.achour_ar.gshglobalactivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogMotifAttente {
    Activity mActivity;
    EditText txt;
    TextView listeMotif;

    public void showDialog(final Activity activity, final String idTicket, final String description){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_motif_attente);

        txt = (EditText) dialog.findViewById(R.id.ETmotif);
        txt.setEnabled(false);

        listeMotif = (TextView) dialog.findViewById(R.id.listeMotif);


        listeMotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setEnabled(false);
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
                builderSingle.setTitle("Sélectionnez un motif");


                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,
                        android.R.layout.select_dialog_singlechoice);

                arrayAdapter.add("Appel négatif");
                arrayAdapter.add("Manque d'équipement");
                arrayAdapter.add("Congé");
                arrayAdapter.add("Fin de semaine");
                arrayAdapter.add("Autre");


                builderSingle.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        if (strName.equals("Autre")){
                            txt.setEnabled(true);
                            txt.requestFocus();
                            txt.setText("");
                            txt.setHint("Spécifiez un motif");

                        }
                        else {
                            txt.setText(strName);
                        }


                    }
                });
                builderSingle.show();
            }
        });



        Button FermerDialog = (Button) dialog.findViewById(R.id.fermer);
        Button SaveBtn = (Button) dialog.findViewById(R.id.saveMotifBtn);

        FermerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt.getText().toString().equals("")){
                    txt.setError("Veuillez donner une raison");
                    txt.requestFocus();
                }
                else {
                    String motif = txt.getText().toString().replaceAll("\n","\\\\r\\\\n");
                    Bundle bundle = new Bundle();
                    bundle.putString("motif",motif);
                    bundle.putString("id",idTicket);
                    bundle.putString("description",description);
                    Message msg = new Message();
                    msg.what = 6;
                    msg.setData(bundle);
                    ListTickets.handlerticket.sendMessage(msg); //Save motif
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
