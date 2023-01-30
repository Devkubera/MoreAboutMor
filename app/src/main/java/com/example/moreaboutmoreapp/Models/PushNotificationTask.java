package com.example.moreaboutmoreapp.Models;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class PushNotificationTask extends AsyncTask<String, Void, String> {
    public static String mtype;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected String doInBackground(String... params) {
        try {
            String apiKey = "AAAA6Esuea4:APA91bG_Sgrnkd1ixaKF8XmPXJCB0s6GytWMtvS4o9vGUwpfNM1XaGPZ4lCk1vuaCIwgvkB_KvhIZOgDiuy3z-QQ_2yzs1xRz2ZsZak_4JJVVWdvsS3scEIxEBBvLuaGSu_tx4w4nRD8";
            String token = params[0];
            String title = params[1];
            String type = params[2];

            // find type
            String types = seperateType(type);
            title = title + types;

            // make body...
            String body = "";

            Log.d("TITLE IS ", "doInBackground: " + title);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "key=" + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            String strJsonBody = "{"
                    + "\"to\":\"" + token + "\","
                    + "\"notification\":{"
                    + "\"title\":\"" + title + "\","
                    + "\"body\":\""+ title +"\""
                    + "}"
                    + "}";
            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);
            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);
            if (httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                System.out.println("scanner: " + scanner);

                // push to firebase
                System.out.println("CHECK TITLE PLEASE " + title);
                pushToFirebase(type,title,body);

                return scanner.useDelimiter("\\A").next();
            } else {
                return "";
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "Error";
    }

    public String seperateType(String type) {

        if (type.equals("like post")) {
            mtype = " ถูกใจโพสต์ของคุณ";
        } else if (type.equals("like comment")) {
            mtype = " ถูกใจความคิดเห็นของคุณ";
        }

        return mtype;
    }

    public void pushToFirebase(String type, String title, String subtitle) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        databaseReference = firebaseDatabase.getReference("NotificationCenter")
                .child(uid);


        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateTime = formatter.format(new Date());

        NotificationData notificationData = new NotificationData(type,title,subtitle,dateTime);

        databaseReference.setValue(notificationData);
    }
}
