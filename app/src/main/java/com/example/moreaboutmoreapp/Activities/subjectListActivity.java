package com.example.moreaboutmoreapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moreaboutmoreapp.Models.User;
import com.example.moreaboutmoreapp.Models.subjectNameModel;
import com.example.moreaboutmoreapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class subjectListActivity extends AppCompatActivity {

    ImageView btn_back;
    RelativeLayout
            btn_addData1,
            subject_faculty_list,
            subject_faculty_list2,
            subject_faculty_bg,
            subject_faculty_bg2;
    Button btn_addSubject;
    TextView drawable, noDrawable;
    TextView drawable2, noDrawable2;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        ChangeUI("1");
        ChangeUI("2");

        noDrawable = findViewById(R.id.subject_txt1);

        drawable = findViewById(R.id.subject_edit_btn);
        drawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubjectListSearchActivity.class);
                startActivity(intent);
            }
        });

        noDrawable2 = findViewById(R.id.subject_txt2);

        drawable2 = findViewById(R.id.subject_edit_btn2);


        // Relative layout text list in Faculty Skill
        subject_faculty_list = findViewById(R.id.subject_txt_list1);

        // Relative layout alpha text in Faculty Skill
        subject_faculty_bg = findViewById(R.id.subject_background_default1);

        // Relative layout text list in GE
        subject_faculty_list2 = findViewById(R.id.subject_txt_list2);

        // Relative layout alpha text in GE
        subject_faculty_bg2 = findViewById(R.id.subject_background_default2);

        // back to setting page
        btn_back = findViewById(R.id.Btn_BackSubjectList);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // go to new activity to search subject
        btn_addData1 = findViewById(R.id.subject_background_default1);
        btn_addData1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubjectListSearchActivity.class);
                startActivity(intent);
                // finish();

            }
        });


    }

    private void ChangeUI(String s) {

        // get the data from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("allSubject");
        //
        if (s.equals("1")) {
            // 1
            ref = ref.child("MajorSkillBranch");
        } else {
            // 2
            ref = ref.child("GeRelax");
        }

        // get user data
        getUserData(ref,s);

        //
    }

    private void getUserData(DatabaseReference ref, String s) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("userData").child(auth.getUid());

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    sendUser(user);
                }
            }

            private void sendUser(User user) {
                String faculty = user.getMajor();
                String branch = user.getSubMajor();

                // setting reference
                DatabaseReference refs = ref.child(faculty).child(branch).child(auth.getUid());

                // get data from this reference
                refs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // check data exits
                        if (snapshot.exists()) {
                            // Set up data
//                            List<String> arrData = new ArrayList<>();
//                            arrData.add(snapshot.getValue().toString());

                            subjectNameModel subjectNameModel = snapshot.getValue(com.example.moreaboutmoreapp.Models.subjectNameModel.class);

                            TextView textView1;
                            TextView textView2;
                            TextView textView3;
                            TextView textView4;
                            TextView textView5;

                            // check type that we work
                            if (s.equals("1")) {
                                // set button and alpha text is gone
                                subject_faculty_bg.setVisibility(View.GONE);
                                // set text faculty list text is show
                                subject_faculty_list.setVisibility(View.VISIBLE);

                                textView1 = findViewById(R.id.subject_list_inbox_1);
                                textView2 = findViewById(R.id.subject_list_inbox_2);
                                textView3 = findViewById(R.id.subject_list_inbox_3);
                                textView4 = findViewById(R.id.subject_list_inbox_4);
                                textView5 = findViewById(R.id.subject_list_inbox_5);

                                // Text View Have Drawable will visible
                                noDrawable.setVisibility(View.GONE);
                                drawable.setVisibility(View.VISIBLE);

                                if (subjectNameModel.getName4().equals("none")) {

                                } else {
                                    textView4.setText("4th " + subjectNameModel.getName4());
                                }

                                if (subjectNameModel.getName5().equals("none")) {

                                } else {
                                    textView5.setText("5th " + subjectNameModel.getName5());
                                }

                                // change UI HERE
                                textView1.setText("1st " + subjectNameModel.getName1());
                                textView2.setText("2nd " + subjectNameModel.getName2());
                                textView3.setText("3rd " + subjectNameModel.getName3());
                            } else {
                                // set button and alpha text is gone
                                subject_faculty_bg2.setVisibility(View.GONE);
                                // set text faculty list text is show
                                subject_faculty_list2.setVisibility(View.VISIBLE);

                                textView1 = findViewById(R.id.subject_list_inbox_1_2);
                                textView2 = findViewById(R.id.subject_list_inbox_2_2);
                                textView3 = findViewById(R.id.subject_list_inbox_3_2);
                                textView4 = findViewById(R.id.subject_list_inbox_4_2);
                                textView5 = findViewById(R.id.subject_list_inbox_5_2);

                                // Text View Have Drawable will visible
                                noDrawable2.setVisibility(View.GONE);
                                drawable2.setVisibility(View.VISIBLE);

                                if (subjectNameModel.getName4().equals("none")) {

                                } else {
                                    textView4.setText("4th " + subjectNameModel.getName4());
                                }

                                if (subjectNameModel.getName5().equals("none")) {

                                } else {
                                    textView5.setText("5th " + subjectNameModel.getName5());
                                }

                                // change UI HERE
                                textView1.setText("1st " + subjectNameModel.getName1());
                                textView2.setText("2nd " + subjectNameModel.getName2());
                                textView3.setText("3rd " + subjectNameModel.getName3());
                            }



//                            for (int i=0; i<arrData.size(); i++) {
//                                Log.d("CHECK SNAPSHOTS", "onDataChange: " + arrData.get(i));
//
//                                // change UI HERE
//                                if (i==0) textView1.setText("1st " + arrData.get(i));
//                                if (i==1) textView2.setText("2nd " + arrData.get(i));
//                                if (i==2) textView3.setText("3rd " + arrData.get(i));
//                                if (i==3) textView4.setText("4th " + arrData.get(i));
//                                if (i==4) textView5.setText("5th " + arrData.get(i));
//
//                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // using value listener to handle problem that we don't have the name of data in each attribute
//                ValueEventListener valueEventListener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
//                        List<String> items = new ArrayList<>(map.values());
//                        //System.out.println(items); // prints "["AAA", "BBB"]"
//                        Log.d("SHOW UP DATA", "onDataChange: " + items);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        System.out.println("Error: " + error.getMessage());
//                    }
//                };
//                ref.addListenerForSingleValueEvent(valueEventListener);
//                Log.d("REF", "sendUser: " + refs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}