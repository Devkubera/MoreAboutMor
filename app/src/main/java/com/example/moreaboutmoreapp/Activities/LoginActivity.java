package com.example.moreaboutmoreapp.Activities;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.ForgetPassFragment;
import com.example.moreaboutmoreapp.HomeFragment;
import com.example.moreaboutmoreapp.R;
import com.example.moreaboutmoreapp.RegisterFragment;
import com.example.moreaboutmoreapp.Utility.NetworkChangerListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    ImageView IMG_Logo, IMG_name, imgMail, imgLock;
    TextView textEmail, textPass, textForget, textHaveAcc, C_Register;
    MaterialCardView cardView;
    EditText loginEmail, loginPass;
    Button loginButton;
    private ProgressBar loadingProgress;
    private FirebaseAuth mAuth;

    NetworkChangerListener networkChangerListener = new NetworkChangerListener();

    @Override
    protected void onStart() {
        super.onStart();
        //Check Network
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangerListener, intentFilter);

        //Check Login
        FirebaseUser user  = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            UpdateUI();
        }

        //Check EmailVerified
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if (user.isEmailVerified()) {
            //UpdateUI();
        //} else {

        //}

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangerListener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));

        mAuth = FirebaseAuth.getInstance();

        IMG_Logo = findViewById(R.id.IMG_Logo);
        IMG_name = findViewById(R.id.IMG_name);
        cardView = findViewById(R.id.layout_from);

        imgMail = findViewById(R.id.imgMail);
        imgLock = findViewById(R.id.imgLock);

        textEmail = findViewById(R.id.textEmail);
        textPass = findViewById(R.id.textPass);
        textHaveAcc = findViewById(R.id.textHaveAcc);

        loginEmail = findViewById(R.id.loginEmail);
        loginPass = findViewById(R.id.loginPass);

        loadingProgress = findViewById(R.id.progressBarLogin);
        loadingProgress.setVisibility(View.INVISIBLE);

        //Click ForgetPass
        textForget = findViewById(R.id.textForget);

        // Make underline
        SpannableString content = new SpannableString("ลืมรหัสผ่าน ?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textForget.setText(content);


        textForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this, "Click Forget!", Toast.LENGTH_SHORT).show();

                //Remove
                IMG_Logo.setVisibility(View.GONE);
                IMG_name.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);

                imgMail.setVisibility(View.GONE);
                imgLock.setVisibility(View.GONE);

                textEmail.setVisibility(View.GONE);
                textPass.setVisibility(View.GONE);
                textForget.setVisibility(View.GONE);
                textHaveAcc.setVisibility(View.GONE);
                loginEmail.setVisibility(View.GONE);
                loginPass.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                C_Register.setVisibility(View.GONE);

                RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.frag);
                bgElement.setBackgroundColor(getResources().getColor(R.color.nav_color));

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag, new ForgetPassFragment()).commit();

            }
        });

        //Click Login
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this, "Click Login!", Toast.LENGTH_SHORT).show();
                loadingProgress.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                final String Email = loginEmail.getText().toString();
                final String Pass = loginPass.getText().toString();

                if (Email.isEmpty() || Pass.isEmpty()) {
                    showMessage("Please Verify All Field");
                    loadingProgress.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                } else {
                    SignIn(Email, Pass);
                }

            }
        });

        //Click Register
        C_Register = findViewById(R.id.textRegister);

        // Make underline
        SpannableString register_text = new SpannableString("สมัครสมาชิก");
        register_text.setSpan(new UnderlineSpan(), 0, register_text.length(), 0);
        C_Register.setText(register_text);

        C_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this, "Click Register!", Toast.LENGTH_SHORT).show();

               //Remove
                IMG_Logo.setVisibility(View.GONE);
                IMG_name.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);

                imgMail.setVisibility(View.GONE);
                imgLock.setVisibility(View.GONE);

                textEmail.setVisibility(View.GONE);
                textPass.setVisibility(View.GONE);
                textForget.setVisibility(View.GONE);
                textHaveAcc.setVisibility(View.GONE);
                loginEmail.setVisibility(View.GONE);
                loginPass.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                C_Register.setVisibility(View.GONE);

                RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.frag);
                bgElement.setBackgroundColor(getResources().getColor(R.color.nav_color));

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag, new RegisterFragment()).commit();

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(RegisterFragment.backKeyPressedListener != null) {
            RegisterFragment.backKeyPressedListener.onBackPressed();
        } else if (ForgetPassFragment.backKeyPressedListener != null) {
            ForgetPassFragment.backKeyPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    //Function
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void SignIn(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            loadingProgress.setVisibility(View.INVISIBLE);
                            loginButton.setVisibility(View.VISIBLE);

                            //Check EmailVerified
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                UpdateUI();
                            } else {
                                user.sendEmailVerification();
                                Toast.makeText(getApplicationContext(), "Check your email to verified your account! ", Toast.LENGTH_LONG).show();
                            }

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error : "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loadingProgress.setVisibility(View.INVISIBLE);
                            loginButton.setVisibility(View.VISIBLE);

                        }

                    }
                });

    }

    private void UpdateUI() {

        //Remove
        IMG_Logo.setVisibility(View.GONE);
        IMG_name.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);

        imgMail.setVisibility(View.GONE);
        imgLock.setVisibility(View.GONE);

        textEmail.setVisibility(View.GONE);
        textPass.setVisibility(View.GONE);
        textForget.setVisibility(View.GONE);
        textHaveAcc.setVisibility(View.GONE);
        loginEmail.setVisibility(View.GONE);
        loginPass.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        C_Register.setVisibility(View.GONE);

        RelativeLayout bgElement = (RelativeLayout) findViewById(R.id.frag);
        bgElement.setBackgroundColor(getResources().getColor(R.color.nav_color));

        //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.replace(R.id.frag, new HomeFragment()).commit();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }



}