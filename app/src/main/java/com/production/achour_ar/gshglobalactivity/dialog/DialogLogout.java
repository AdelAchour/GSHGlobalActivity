package com.production.achour_ar.gshglobalactivity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.production.achour_ar.gshglobalactivity.R;
import com.production.achour_ar.gshglobalactivity.activity.AccueilUser;

public class DialogLogout {
    Activity mActivity;

    public void showDialog(Activity activity, String name){
        mActivity = activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_logout);

        TextView txt = (TextView)dialog.findViewById(R.id.messagealert);
        txt.setText(name+", "+txt.getText());


        Button FermerDialog = (Button) dialog.findViewById(R.id.fermer);
        Button LogoutBtn = (Button) dialog.findViewById(R.id.logoutBtn);

        FermerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccueilUser.handler.sendEmptyMessage(0); //logout
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
