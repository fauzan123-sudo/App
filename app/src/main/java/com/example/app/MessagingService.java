package com.example.app;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.helper.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("remote", "From: " + remoteMessage.getFrom());

//        try {
//            JSONObject json = new JSONObject(remoteMessage.getData().toString());
//            sendPushNotification(json);
//        } catch (Exception e) {
//            Log.e("eror", "Exception: " + e.getMessage());
//        }

        if (remoteMessage.getData().size() > 0) {
            Log.d("Remote", "Message data payload: " + remoteMessage.getData());
            String title    = remoteMessage.getNotification().getTitle();
            String text     = remoteMessage.getNotification().getBody();
            String sound    = remoteMessage.getNotification().getSound();
            NotificationHelper.displayNotification(getApplicationContext(), title, text, sound);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }
        if (remoteMessage.getNotification() != null){
            String title    = remoteMessage.getNotification().getTitle();
            String text     = remoteMessage.getNotification().getBody();
            String sound    = remoteMessage.getNotification().getSound();
            Uri image       = remoteMessage.getNotification().getImageUrl();
            Log.d("sound", "onMessageReceived: " + sound);
            Log.d("gambar", "onMessageReceived: "+image);

            //calling method to display notification
            NotificationHelper.displayNotification(getApplicationContext(), title, text, sound);
//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                sendPushNotification(json);
//            } catch (Exception e) {
//                Log.e("eror", "Exception: " + e.getMessage());
//            }
        }
    }

//    private void sendPushNotification(JSONObject json) {
//        //optionally we can display the json into log
//        Log.e("error", "Notification JSON " + json.toString());
//        try {
//            //getting the json data
//            JSONObject data = json.getJSONObject("title");
//            Log.d("title", "sendPushNotification: "+ data);
//
//            //parsing json data
//            String title        = data.getString("title");
//            String message      = data.getString("body");
//            String imageUrl     = data.getString("image");
//            Log.d("title", "sendPushNotification: "+title);
//
//            //creating MyNotificationManager object
//            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
//
//            //creating an intent for the notification
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//
//            //if there is no image
//            if(imageUrl.equals("null")){
//                //displaying small notification
//                mNotificationManager.showSmallNotification(title, message, intent);
//            }else{
//                //if there is an image
//                //displaying a big notification
//                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
//            }
//        } catch (JSONException e) {
//            Log.e("error", "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e("error", "Exception: " + e.getMessage());
//        }
//    }
}

