package com.example.app.helper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.app.Dashboard;
import com.example.app.Login;
import com.example.app.R;

public class NotificationHelper {
    public static void displayNotification(Context context, String title, String text, String sound){
        Intent activityIntent = new Intent(context, Login.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);
        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", title);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constans.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setSound(Uri.parse(sound))
                .addAction(R.mipmap.ic_launcher, "Cek Informasi ", actionIntent)
                .setSmallIcon(R.drawable.ic_launcher_background);
        Log.d("sound", "displayNotification: " + sound);

        mBuilder.setSound(Uri.parse(sound));
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(1,mBuilder.build());
    }
}
