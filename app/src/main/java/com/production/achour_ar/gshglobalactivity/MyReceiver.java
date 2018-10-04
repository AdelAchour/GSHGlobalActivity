package com.production.achour_ar.gshglobalactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        // check the action equal to the action we fire in broadcast,
        if(intent.getAction().equalsIgnoreCase("com.example.Broadcast")){

            NewTicketNotification notification = new NewTicketNotification();
            notification.notify(context, bundle.getString("titre"), Integer.valueOf(bundle.getString("id")));

        }
    }
}
