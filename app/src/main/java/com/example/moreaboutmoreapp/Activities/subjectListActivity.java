package com.example.moreaboutmoreapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.moreaboutmoreapp.R;
import com.example.moreaboutmoreapp.SettingFragment;

public class subjectListActivity extends AppCompatActivity {

    ImageView btn_back;
    RelativeLayout btn_addData1;
    Button btn_addSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

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
}