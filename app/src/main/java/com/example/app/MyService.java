package com.example.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }

    public void tampilNotifikasi(Context context, String judul, String isi, Intent intent){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, Login.class);
        PendingIntent pi = PendingIntent.getActivity(this,1,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String ChannelID    = "channel_notif";
        String ChannelName  = "Channel_Name";

        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(ChannelID, ChannelName,importance);
            notificationManager.createNotificationChannel(mChannel);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, ChannelID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(judul)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(isi))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentText(isi);

            Log.d("ada", "tampilNotifikasi: " + judul);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);
            notificationManager.notify(0, mBuilder.build());
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Notifikasi", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            //ambil judul dan notifikasi
//            String judul  = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
//            String isi    = remoteMessage.getNotification().getBody();
//
//            tampilNotifikasi(getApplicationContext(), judul, isi, new Intent());
//                Log.d("Notifikasi", "onMessageReceived: " + judul + isi);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("Notifikasi", "Message Notification Body: " + remoteMessage.getNotification().getBody());


//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                sendPushNotification(json);
//            } catch (Exception e) {
//                Log.e(TAG, "Exception: " + e.getMessage());
//            }
        }

    }
}

