package com.example.moreaboutmoreapp.Models;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.moreaboutmoreapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationCenter extends FirebaseMessagingService {
    // Declaration space
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference notificationToken;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle message received from Firebase here
    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        // Handle new token received from Firebase here
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        Log.d("Testing generate token", "onNewToken: ");

        /** This function is automatically work when existing token is changed */
        // save token to sqlite0
        saveToken(token);
    }

    private void saveToken(String token) {
        String sqlName = getString(R.string.token_sqlite);
        SharedPreferences sharedPreferences = getSharedPreferences(sqlName, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void sendRegistrationToServer(String token) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String path = "tokens/" + uid +"/";
        notificationToken = firebaseDatabase.getReference(path);
        notificationToken.child("token").setValue(token);
    }
}
