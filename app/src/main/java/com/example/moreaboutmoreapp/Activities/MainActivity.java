package com.example.moreaboutmoreapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.moreaboutmoreapp.HomeFragment;
import com.example.moreaboutmoreapp.MenuFragment;
import com.example.moreaboutmoreapp.Models.GeLink;
import com.example.moreaboutmoreapp.Models.ModelFacultyData;
import com.example.moreaboutmoreapp.Models.User;
import com.example.moreaboutmoreapp.NotificationFragment;
import com.example.moreaboutmoreapp.R;
import com.example.moreaboutmoreapp.RegisterFragment;
import com.example.moreaboutmoreapp.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    // For menu GE Course
    public String subMajor;
    public String geLink;

    // For Api
    private static String studyApiURL = "https://script.google.com/macros/s/AKfycbwKcPctrw2a_L90xtp32dXMr30XRNRoQ9OIw2-gMKhk83pU23Vv25XEsEAvFLfWEPkO/exec";
    private static String geApiURL = "https://script.google.com/macros/s/AKfycbyZWz1QLjtkmWBnDPHE1r5YSYUK8owLJJlEZhXRbklL49Mhx01GWaK5oC9PZIIuKHF1/exec";

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

        // CALL API STUDY
        FetchingFacultyAPI fetchingFacultyAPI = new FetchingFacultyAPI();
        fetchingFacultyAPI.execute(studyApiURL);

        // CALL API GE
        FetchingGeAPI ge = new FetchingGeAPI();
        ge.execute(geApiURL);

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

        getFragmentManager().popBackStack();



    }

    // For Menu Study Plan, GE and Study Course Description

    private class FetchingFacultyAPI extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            BufferedReader reader;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    String[] arrayCut = new String[1];

                    // Read the response from the API
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    reader.close();

                    // Parse the response using Gson
                    // Using ModelFacultyData.java
                    Gson gson = new Gson();
                    Type itemListType = new TypeToken<List<ModelFacultyData>>() {}.getType();
                    List<ModelFacultyData> items = gson.fromJson(result.toString(), itemListType);

                    // Use the list of items
                    for (ModelFacultyData item : items) {
                        System.out.println(item.getMajor() + ": $" + item.getFaculty() + "\n");

                        // Check data that matching major of user
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            // Firebase Realtime Database get ref
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("userData/" + uid);
                            //Log.d("FirebaseAuth", "uid: " + uid);

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User data = snapshot.getValue(User.class);
                                    Log.d("FirebaseAuth ", "onDataChange: " + data.subMajor);
                                    arrayCut[0] = data.subMajor;
                                    subMajor = arrayCut[0];
                                    System.out.println("major array check : " + arrayCut[0]);

                                    if (arrayCut[0].equals(item.getMajor())) {
                                        sqLiteStudyPlan(item);
                                        Log.d("Check sharePreference : ", "major : " + item.getMajor());
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Error fetching study course API : ", e.getMessage(), e);
                return null;
            }
            return reader.toString();
        }
        protected void onPostExecute (String response) {

        }
    }

    private class FetchingGeAPI extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            BufferedReader reader;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    String[] arrayCut = new String[1];

                    // Read the response from the API
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    reader.close();

                    // parse json to string
                    Gson gson = new Gson();
                    Type itemListType = new TypeToken<List<GeLink>>() {}.getType();
                    List<GeLink> items = gson.fromJson(result.toString(), itemListType);

                    // fetch data from api and store in variable
                    for (GeLink data : items) {
                        geLink = data.getLink();
                        System.out.println("geLink in data : " + geLink);
                    }

                    // check null
                    if (!geLink.equals(null)) {
                        sqLiteGe(geLink);
                    }

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("Error fetching study course API : ", e.getMessage(), e);
                return null;
            }
            return reader.toString();
        }
        protected void onPostExecute (String response) {

        }
    }

    private void sqLiteStudyPlan(ModelFacultyData item) {

        System.out.println("subMajor : " + subMajor);

        // Declare SharePreference
        SharedPreferences sharedPreferences = getSharedPreferences("studyPlans", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("CourseDescription", item.getCourseDescription());
        editor.putString("Major", item.getMajor());
        editor.putString("Plan", item.getStudyPlan());
        editor.apply();

        String x = sharedPreferences.getString("Major", "หาไม่เจอจ้า");
        System.out.println("x " + x);
    }

    private void sqLiteGe(String link) {

        // Declare SharePreference
        SharedPreferences sharedPreferences = getSharedPreferences("geApi", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("link", link);
        editor.apply();

        String x = sharedPreferences.getString("link", "หาไม่เจอจ้า");
        System.out.println("ge link in sqlite " + x);
    }

    // End of 3 Menu Code Study Plan, Ge Course Description, Major Course Description



}