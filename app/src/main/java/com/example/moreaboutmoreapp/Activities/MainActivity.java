package com.example.moreaboutmoreapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.moreaboutmoreapp.HomeFragment;
import com.example.moreaboutmoreapp.MenuFragment;
import com.example.moreaboutmoreapp.NotificationFragment;
import com.example.moreaboutmoreapp.R;
import com.example.moreaboutmoreapp.RegisterFragment;
import com.example.moreaboutmoreapp.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));

        OpenFrag(new HomeFragment());
        bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


    }

    public boolean OpenFrag(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag, fragment)
                    .commit();
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_Home:
                fragment = new HomeFragment();
                break;

            case R.id.nav_Notification:
                fragment = new NotificationFragment();
                break;

            case R.id.nav_Menu:
                fragment = new MenuFragment();
                break;

            case R.id.nav_Setting:
                fragment = new SettingFragment();
                break;

        }

        return OpenFrag(fragment);
    }

    //onBackPressed
    private boolean isPressed = false;

    @Override
    public void onBackPressed() {


        if (bottomNavigationView.getSelectedItemId() == R.id.nav_Home) {

            /*if(HomeFragment.backKeyPressedListener != null) {
                //super.onBackPressed();
                //inish();
                //System.exit(0);
                moveTaskToBack(true);
                //HomeFragment.backKeyPressedListener.onBackPressed();
            }*/

            if (isPressed) {
                moveTaskToBack(true);
                return;
            }

            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            isPressed = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isPressed = false;
                }
            }, 2000);



        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_Home);
            //moveTaskToBack(true);
        }


    }


}