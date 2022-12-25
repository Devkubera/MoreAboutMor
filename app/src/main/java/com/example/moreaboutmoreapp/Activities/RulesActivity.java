package com.example.moreaboutmoreapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Adapters.RulesAdapter;
import com.example.moreaboutmoreapp.R;

public class RulesActivity extends AppCompatActivity {

    ViewPager mSlideImgRules;
    LinearLayout mDotSlide;
    Button backBtn, nextBtn, startButton;

    TextView[] dot;
    RulesAdapter rulesAdapter;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));


        backBtn = findViewById(R.id.backButton);
        nextBtn = findViewById(R.id.nextButton);
        startButton = findViewById(R.id.startButton);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getItem(0) > 0) {

                    mSlideImgRules.setCurrentItem(getItem(-1), true);

                }

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getItem(0) < 6) {

                    mSlideImgRules.setCurrentItem(getItem(1), true);

                }

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RulesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSlideImgRules = (ViewPager) findViewById(R.id.slideRules);
        mDotSlide = (LinearLayout) findViewById(R.id.LayoutCountRules);

        rulesAdapter = new RulesAdapter(this);

        mSlideImgRules.setAdapter(rulesAdapter);

        setupCountDot(0);
        mSlideImgRules.addOnPageChangeListener(viewListener);

    }

    public void setupCountDot(int position) {

        dot = new TextView[7];
        mDotSlide.removeAllViews();

        for (int i = 0; i < dot.length; i++) {
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("&#8226"));
            dot[i].setTextSize(40);
            dot[i].setTextColor(getResources().getColor(R.color.white));
            mDotSlide.addView(dot[i]);
        }

        dot[position].setTextColor(getResources().getColor(R.color.orange));

    }


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setupCountDot(position);
            mCurrentPage = position;

            /*if (position > 0) {
                backBtn.setVisibility(View.VISIBLE);
            } else {
                backBtn.setVisibility(View.INVISIBLE);
            }*/

            if (position == 0) {
                startButton.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);

            } else if (position == dot.length - 1) {
                startButton.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.INVISIBLE);

            } else {
                startButton.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
            }



        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {

        return mSlideImgRules.getCurrentItem() + i;

    }


}