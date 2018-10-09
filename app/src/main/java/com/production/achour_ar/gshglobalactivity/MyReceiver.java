package com.production.achour_ar.gshglobalactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getBundleExtra("bundle");
        // check the action equal to the action we fire in broadcast,
        System.out.println("I'm in the receiver");
        if(intent.getAction().equalsIgnoreCase("com.example.Broadcast")){

            System.out.println("getAction right baby, i'm gonna send a notif !");
            System.out.println("titre: "+bundle.getString("titre")+" & id: "+bundle.getInt("id"));
            NewTicketNotification notification = new NewTicketNotification();

            notification.notify(context,
                                bundle,
                                bundle.getInt("id")
                                );

        }
    }
}
