package com.production.achour_ar.gshglobalactivity.ITs.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.production.achour_ar.gshglobalactivity.R;

public class DialogNoTicket {
    Activity mActivity;

    public void showDialog(Activity activity){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_noticket);


        Button FermerDialog = (Button) dialog.findViewById(R.id.fermer);
        FermerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
