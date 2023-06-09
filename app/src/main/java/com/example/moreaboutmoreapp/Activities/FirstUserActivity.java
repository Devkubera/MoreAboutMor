package com.example.moreaboutmoreapp.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Adapters.FirstUserAdapter;
import com.example.moreaboutmoreapp.Models.NotificationCenter;
import com.example.moreaboutmoreapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirstUserActivity extends AppCompatActivity {

    ViewPager mSlideImgInfo;
    LinearLayout mDotSlide;
    Button nextBtn, startButton;

    TextView[] dot;
    FirstUserAdapter firstUserAdapter;

    SharedPreferences preferences;

    // For notification declaration space
    NotificationCenter notificationCenter;



    @Override
    protected void onStart() {
        super.onStart();

        //Check First User
        preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String FirstUser = preferences.getString("SaveUser", "");

        if (FirstUser.equals("Yes")) {

            Intent intent = new Intent(FirstUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_user);

        /** Not use this function */
        // get token for FCM
        //getTokenFCM();

        // create new token and save to sqlite
        //createTokenFCM();

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));


        nextBtn = findViewById(R.id.nextButtonInfo);
        startButton = findViewById(R.id.startButtonInfo);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getItem(0) < 3) {

                    mSlideImgInfo.setCurrentItem(getItem(1), true);

                }

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FirstUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                //Save First User
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SaveUser", "Yes");
                editor.apply();


            }
        });

        mSlideImgInfo = (ViewPager) findViewById(R.id.slideInfo);
        mDotSlide = (LinearLayout) findViewById(R.id.LayoutCountInfo);


        firstUserAdapter = new FirstUserAdapter(this);

        mSlideImgInfo.setAdapter(firstUserAdapter);

        setupCountDot(0);
        mSlideImgInfo.addOnPageChangeListener(viewListener);



    } // on Create

    // create new token FCM
    private void createTokenFCM() {
//        notificationCenter = new NotificationCenter();
//        /** send token to the servers */
//        notificationCenter.onNewToken(token);
    }



    public void setupCountDot(int position) {

        dot = new TextView[4];
        mDotSlide.removeAllViews();

        for (int i = 0; i < dot.length; i++) {
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("&#8226"));
            dot[i].setTextSize(35);
            dot[i].setTextColor(getResources().getColor(R.color.grey_font));
            mDotSlide.addView(dot[i]);
        }

        dot[position].setTextColor(getResources().getColor(R.color.nav_color));

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setupCountDot(position);

            if (position == 0) {
                startButton.setVisibility(View.INVISIBLE);

            } else if (position == dot.length - 1) {
                startButton.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.INVISIBLE);

            } else {
                startButton.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {

        return mSlideImgInfo.getCurrentItem() + i;

    }

}