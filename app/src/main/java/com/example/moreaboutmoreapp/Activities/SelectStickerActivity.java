package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moreaboutmoreapp.Models.Comment;
import com.example.moreaboutmoreapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import pl.droidsonroids.gif.GifImageView;

public class SelectStickerActivity extends AppCompatActivity {

    GifImageView selectSticker;
    ProgressBar progressBarSticker;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    String getSticker, postKey;
    static String COMMENT_KEY = "Comment";
    SharedPreferences preferences;

    Button commentButton;
    ImageView Btn_BackSticker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sticker);

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Get StickerUrl and Set
        getSticker = getIntent().getExtras().getString("StickerUrl");

        //Get PostKey
        preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        postKey = preferences.getString("SavePostKey", "");

        LayoutInflater layoutInflater = LayoutInflater.from(SelectStickerActivity.this);
        View view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_select_sticker, null);

        //Create AlertDialog
        AlertDialog builder = new AlertDialog.Builder(SelectStickerActivity.this)
                .setCancelable(false)
                .setView(view)
                .create();

        progressBarSticker = view.findViewById(R.id.progressBarSticker);
        selectSticker = view.findViewById(R.id.selectSticker);
        Glide.with(getApplicationContext()).load(getSticker).into(selectSticker);

        //Back to Select Sticker
        Btn_BackSticker = view.findViewById(R.id.Btn_BackSticker);
        Btn_BackSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Comment Sticker
        commentButton = view.findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCommentSticker();
            }
        });


        builder.show();


    }

    //Function
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void addCommentSticker() {
        //Get_Uri Profile and Name
        DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(firebaseUser.getUid());
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("userPhoto").getValue().toString();
                String name = snapshot.child("name").getValue().toString();

                loadImageNameUser(image,name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImageNameUser(String image, String name) {
        Comment comment = new Comment(getSticker, firebaseUser.getUid(), firebaseUser.getDisplayName(), name, image,"sticker");

        //Add Post to firebase
        addComment(comment);
    }

    private void addComment(Comment comment) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference(COMMENT_KEY).child(postKey).push();

        //Get PostKey ID
        String key = myRef.getKey();
        comment.setCommentKey(key);
        comment.setPostKey(postKey);

        //Add Comment data to firebase database
        myRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //showMessage("Comment added successfully");
                commentButton.setVisibility(View.VISIBLE);
                progressBarSticker.setVisibility(INVISIBLE);

                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to added comment : " +e.getMessage());
            }
        });
    }


}