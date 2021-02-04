package com.production.achour_ar.gshglobalactivity.ITs.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.production.achour_ar.gshglobalactivity.ITs.activity.CheckAuth;
import com.production.achour_ar.gshglobalactivity.ITs.data_model.UserModel;
import com.production.achour_ar.gshglobalactivity.R;

import java.util.concurrent.ThreadLocalRandom;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data Received: " + remoteMessage.getData());

        String title = remoteMessage.getData().get("title");
        String titleTicket = remoteMessage.getData().get("ticket_title");
        String idTicket = remoteMessage.getData().get("id");

        if (title != null) {
            if (title.equals("New ticket")) sendNotificationTicket(titleTicket, idTicket);
        }


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance(getApplicationContext()).beginWith(work).enqueue();
        // [END dispatch_job]
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    private void sendNotificationTicket(String titleTicket, String idTicket) {
        Intent intent = new Intent(this, CheckAuth.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.new_ticket_128);

        String messageBody = "Un nouveau ticket vous a été attribué.\n\nNom du ticket : " + titleTicket + "\nId du ticket : " + idTicket;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.new_ticket_32)
                        .setContentTitle("Nouveau ticket")
                        .setContentText(messageBody).setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody)
                        .setBigContentTitle("Nouveau ticket")
                        .setSummaryText("Info Helpdesk"))
                        .setLargeIcon(picture)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000})
                        .setColor(Color.parseColor("#ffd21d"))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        int randomNum = ThreadLocalRandom.current().nextInt(0, 60 + 1);
        notificationManager.notify(randomNum, notificationBuilder.build());
    }
}
