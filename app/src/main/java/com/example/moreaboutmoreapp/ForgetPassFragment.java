package com.example.moreaboutmoreapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgetPassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgetPassFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgetPassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgetPassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgetPassFragment newInstance(String param1, String param2) {
        ForgetPassFragment fragment = new ForgetPassFragment();
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

    @Override
    public void onPause() {
        super.onPause();
        backKeyPressedListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        backKeyPressedListener = this;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        //Clear Activity Stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    ImageView Btn_Back;
    private EditText putEmail;
    private Button sendButton;
    private ProgressBar loadingProgress;
    private FirebaseAuth mAuth;
    public static BackKeyPressedListener backKeyPressedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forget_pass, container, false);

        //loadingProgress
        loadingProgress = view.findViewById(R.id.progressBar);

        //Button Back
        Btn_Back = view.findViewById(R.id.Btn_BackForgetPage);
        Btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                //Clear Activity Stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //Send Email
        putEmail = view.findViewById(R.id.putEmail);

        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendButton.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                resetPassword();

            }
        });

        return view;
    }



    //Function
    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void resetPassword() {
        String email = putEmail.getText().toString().trim();

        if (email.isEmpty()) {
            putEmail.setError("Email is required!");
            putEmail.requestFocus();
            sendButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(View.INVISIBLE);
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            putEmail.setError("Please provide valid email!");
            putEmail.requestFocus();
            sendButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(View.INVISIBLE);
            return;
        }

        loadingProgress.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    AlertBox();
                } else {
                    showMessage("Try again! Something wrong happened!");
                    sendButton.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    //AlertBox
    private void AlertBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.reset_passwoed_dialog, null);

        //Create AlertDialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        //Click To Login Activity
        Button click_ok = view.findViewById(R.id.click_ok);
        click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendButton.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.INVISIBLE);

                //Clear EditText
                putEmail.getText().clear();

                //Close AlertDialog
                builder.dismiss();
            }
        });

        builder.show();

    }


}