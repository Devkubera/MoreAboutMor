package com.example.moreaboutmoreapp.Models;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.moreaboutmoreapp.Activities.MainActivity;
import com.example.moreaboutmoreapp.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationClass {
    // create Singleton
    private static NotificationClass instance;
    private NotificationClass() {}
    public static NotificationClass getInstance() {
        if (instance == null) {
            instance = new NotificationClass();
        }
        return instance;
    }

    // Declaration
    private static final String CHANNEL_ID = String.valueOf(R.string.channel_id);
    private static final String CHANNEL_NAME = String.valueOf(R.string.channel_name);
    private static final String CHANNEL_DESCRIPTION = String.valueOf(R.string.channel_description);
    private static final int NOTIFICATION_ID = 1;

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(Context context, String title, String message) {
        /** Intent section if we want to navigation to any content when user tap notification*/
        // We will use fragment transaction because we use fragment
        // For Example
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** get a FCM token to specific devices to send notification */
        String token = FirebaseMessaging.getInstance().getToken().toString();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setContentTitle("My notification")
                .setContentText("Hello World!")
//                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // If you are wondering What I don't use notificationManager1
        // Because NotificationManaCompat and NotificationManager is give same result
        // But NotificationManaCompat can support android old version lower android 4.4

        // NotificationManager notificationManager1 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(token,NOTIFICATION_ID, builder.build());
    }

//    public void createChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
//            channel.setDescription(CHANNEL_DESCRIPTION);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//
//            //NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
//            //notificationManager.createNotificationChannel(channel);
//
//        }
//    }

}
