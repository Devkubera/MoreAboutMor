package com.example.moreaboutmoreapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Declare Space for 3 Menu study plan, courseDescription, GE
    Button btn_studyPlan;
    Button btn_courseDes;
    Button btn_GE;
    Button btn_calendar;
    Button btn_ranking;

    // Declare String to store sharepreference 3 Menu study plan, courseDescription, GE
    public static String link_studyplan;
    public static String link_courseDes;
    public static String link_geApi;
    public static String major;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("allPost");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

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

        // For 3 Menu study plan, courseDescription, GE

        // find id study plan button
        btn_studyPlan = view.findViewById(R.id.Btn_SP);
        btn_studyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPref("studyPlans");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link_studyplan));
                startActivity(intent);
            }
        });

        btn_courseDes = view.findViewById(R.id.Btn_MJ);
        btn_courseDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPref("studyPlans");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link_courseDes));
                startActivity(intent);
            }
        });

        btn_GE = view.findViewById(R.id.Btn_GE);
        btn_GE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPref("geApi");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link_geApi));
                startActivity(intent);
            }
        });

        btn_calendar = view.findViewById(R.id.Btn_C);
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://reg3.sut.ac.th/registrar/calendar.asp?schedulegroupid=101&acadyear=2565&semester=3";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        return view;
    }

    public void getPref(String name) {
        // Get data from study plan sharepreference
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(name, MODE_PRIVATE);

        if (name.equals("studyPlans")) {
            String studyPlan = sharedPreferences.getString("Plan", null);
            String course = sharedPreferences.getString("CourseDescription", null);
            link_studyplan = studyPlan;
            link_courseDes = course;

            if (studyPlan != null) {
                System.out.println("link_studyplan = " + studyPlan);
            }
            if (course != null) {
                System.out.println("link_courseDes = " + course);
            }
        }

        if (name.equals("geApi")) {
            link_geApi = sharedPreferences.getString("link", null);

            if (link_geApi != null) {
                System.out.println("link_geApi = " + link_geApi);
            }
        }
    }
}