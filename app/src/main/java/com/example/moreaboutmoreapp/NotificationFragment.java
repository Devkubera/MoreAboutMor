package com.example.moreaboutmoreapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Activities.MainActivity;
import com.example.moreaboutmoreapp.Adapters.NotificationAdapter;
import com.example.moreaboutmoreapp.Models.NotificationData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    List<NotificationData> notificationDataList;

    // for searching uidReceiver
    public static String firebaseKey;
    public static String uidReceiver;


    public NotificationFragment() {
        // Required empty public constructor
    }



    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    CircleImageView userProfile;

    // Notification
    private static final String CHANNEL_ID = String.valueOf(R.string.channel_id);
    private static final String CHANNEL_NAME = String.valueOf(R.string.channel_name);
    private static final String CHANNEL_DESCRIPTION = String.valueOf(R.string.channel_description);
    private static final int NOTIFICATION_ID = 1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // for In-app messaging
        FirebaseInAppMessaging.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("allPost");

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        //Load User Profile
        userProfile = view.findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ProfileActivity = new Intent(getActivity(), com.example.moreaboutmoreapp.Activities.ProfileActivity.class);
                startActivity(ProfileActivity);
            }
        });

        DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
        DatabaseReference imageRef = userDataRef.child("userPhoto");
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String get_URI = snapshot.getValue().toString();
                Picasso.get().load(get_URI).into(userProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Notification Adapter
        /** reverse list to display lasted notification */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        /** end of reverse list */

        recyclerView = view.findViewById(R.id.list_notification);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Declare Firebase Here

        // Android Notification Example
        // createNotificationChannel();
        //NotificationExample();

        // in-app messaging test
        //newMessage();

        // notificationClass.createNotificationChannel(getContext());
        // notificationClass.createNotification(getContext(), "test", "description");


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get Notification List from Firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        uidReceiver = firebaseAuth.getUid();
        databaseReference = firebaseDatabase.getReference("NotificationCenter");

        // Search Data in Firebase
        databaseReference.orderByChild("uidReceiver").equalTo(uidReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationDataList = new ArrayList<>();
                if (snapshot.exists()) {
                    // Log.d("NotyFragment", snapshot.getKey() + ": " + snapshot.getValue());
                    for (DataSnapshot notySnap : snapshot.getChildren()) {
                        System.out.println(snapshot.getValue());
                        NotificationData notyData = notySnap.getValue(NotificationData.class);
                        notificationDataList.add(notyData);
                    }

                    notificationAdapter = new NotificationAdapter(getActivity(), notificationDataList);
                    recyclerView.setAdapter(notificationAdapter);
                }
                else {
                    Toast.makeText(getContext(), "ขณะนี้ยังไม่มีแจ้งเตือนค่ะ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("NotyFragment", "Failed to read value.", error.toException());
            }
        });

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                notificationDataList = new ArrayList<>();
//                if (snapshot.exists()) {
//                    for (DataSnapshot notySnap : snapshot.getChildren()) {
//                        System.out.println(snapshot.getValue());
//                        NotificationData notyData = notySnap.getValue(NotificationData.class);
//                        notificationDataList.add(notyData);
//                    }
//
//                    notificationAdapter = new NotificationAdapter(getActivity(), notificationDataList);
//                    recyclerView.setAdapter(notificationAdapter);
//                }
//                else {
//                    Toast.makeText(getContext(), "ขณะนี้ยังไม่มีแจ้งเตือนค่ะ", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void NotificationExample() {
        /** Intent section if we want to navigation to any content when user tap notification*/
        // We will use fragment transaction because we use fragment
        // For Example
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** get a FCM token to specific devices to send notification */
        String token = FirebaseMessaging.getInstance().getToken().toString();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
//                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(token,NOTIFICATION_ID, builder.build());
    }

    /** Create Notification Channel to Group Notification in setting in android API 26+ */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}