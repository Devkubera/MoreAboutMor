package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.HomeFragment;
import com.example.moreaboutmoreapp.R;
import com.example.moreaboutmoreapp.UploadProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;

public class SetupUserActivity extends AppCompatActivity {

    private TextInputLayout setName;
    private TextView textInfoSetup01, textInfoSetup02;
    private ImageView imageSetup;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ProgressBar loadingProgress;
    private Button setNameButton;

    StepView stepView;

    public static String oldName;



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user);

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        textInfoSetup01 = findViewById(R.id.textInfoSetup01);
        textInfoSetup02 = findViewById(R.id.textInfoSetup02);
        setName = findViewById(R.id.setName);
        imageSetup = findViewById(R.id.imageSetup);
        loadingProgress = findViewById(R.id.progressBar);

        //Step View
        stepView = findViewById(R.id.step_view);
        stepView.getState()
                .selectedTextColor(getResources().getColor(R.color.white))
                .animationType(StepView.ANIMATION_CIRCLE)
                .selectedCircleColor(getResources().getColor(R.color.white))
                .selectedCircleRadius(55)
                .selectedStepNumberColor(getResources().getColor( R.color.nav_color))
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add("ตั้งชื่อเล่น");
                    add("ตั้งรูปโปไฟล์");

                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(2)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .stepLineWidth(8)
                .textSize(50)
                .stepNumberTextSize(55)
                .typeface(ResourcesCompat.getFont(getApplicationContext(), R.font.mitr_light))
                // other state methods are equal to the corresponding xml attributes
                .commit();

        stepView.done(false);

        /** check name database section */

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = mAuth.getUid();
        DatabaseReference reference = firebaseDatabase.getReference("userData").child(uid).child("name");

        // check name of user in database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    oldName = snapshot.getValue().toString();
                    setName.getEditText().setText(oldName);
                } else {
                    Log.e("SHOW Failds IN FETCHING OLD NAME", "onDataChange: " + snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SHOW ERROR IN FETCHING OLD NAME", "onDataChange: " + error);
            }
        });

        setNameButton = findViewById(R.id.setNameButton);
        setNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setNameButton.setVisibility(INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                String Name = setName.getEditText().getText().toString().trim();
                updateUser(Name);
                //finish();

            }
        });



    }

    //Function
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        String nameInput = setName.getEditText().getText().toString().trim();

        if (nameInput.isEmpty()) {
            setName.setError("Field can't be empty");
            setNameButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else if (nameInput.length() > 20) {
            setName.setError("More than 20 characters");
            setNameButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else if (nameInput.equals("default")) {
            setName.setError("Can't use this name");
            setNameButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            setName.setError(null);
            return true;
        }
    }

    private void updateUser(String name) {

        if (!validateName()) {
            return;

        } else {

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("userData").child(currentUser.getUid()).child("name");

            myRef.setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    //OpenFrag
                    OpenFrag(new UploadProfileFragment());

                    //Remove UI Activity
                    RemoveUI();

                }
            });
        }

    }


    public boolean OpenFrag(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_setup, fragment)
                    .commit();
        }

        return true;
    }

    private void RemoveUI() {

        textInfoSetup01.setVisibility(View.GONE);
        textInfoSetup02.setVisibility(View.GONE);
        setName.setVisibility(View.GONE);
        imageSetup.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);
        stepView.setVisibility(View.GONE);


    }

}