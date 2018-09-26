package com.production.achour_ar.gshglobalactivity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class DialogMotifAttente {
    Activity mActivity;

    public void showDialog(Activity activity, final String idTicket, final String description){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_motif_attente);

        final EditText txt = (EditText) dialog.findViewById(R.id.ETmotif);

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
        });

        dialog.show();
    }
}
