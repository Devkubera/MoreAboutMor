package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moreaboutmoreapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.ZoneId;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Calendar;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class CommentDetailActivity extends AppCompatActivity {

    TextView textTime, textUser, textComments, likeCount, commentCount;
    ImageView userProfile, Btn_BackPost;
    GifImageView stickerComment;
    String CommentKey, postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postReference, likeReference, commentReference;

    RelativeLayout layoutReport, layoutEdit, layoutDelete;

    BottomSheetDialog bottomSheetDialog, bottomSheetDialogEditComment;
    Boolean testClick = false;
    private ProgressBar loadingProgress;
    private Button EditCommentButton;

    TextInputLayout Edit_Comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

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

        //Back To Post Detail Activity
        Btn_BackPost = findViewById(R.id.Btn_BackPost);
        Btn_BackPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(PostDetailActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                finish();
            }
        });

        //Get And Set Data
        CommentKey  = getIntent().getExtras().getString("CommentKey");
        postKey = getIntent().getExtras().getString("postKey");

        String getPhoto = getIntent().getExtras().getString("userPhotoImg");
        userProfile = findViewById(R.id.userProfile);
        Picasso.get().load(getPhoto).into(userProfile);

        String getUser = getIntent().getExtras().getString("textUser");
        textUser = findViewById(R.id.textUser);
        textUser.setText(getUser);

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        textTime = findViewById(R.id.textTime);
        textTime.setText(date);

        String getComment = getIntent().getExtras().getString("textComments");
        String getType = getIntent().getExtras().getString("typeComments");
        stickerComment = findViewById(R.id.stickerComment);
        textComments = findViewById(R.id.textComments);

        //Check User for Edit or Delete Post
        layoutReport = findViewById(R.id.layoutReport);
        layoutEdit = findViewById(R.id.layoutEdit);
        layoutDelete = findViewById(R.id.layoutDelete);
        String getEmail = getIntent().getExtras().getString("textEmail");

        if (!getEmail.equals(firebaseUser.getDisplayName())) {
            layoutEdit.setVisibility(View.GONE);
            layoutDelete.setVisibility(View.GONE);
            layoutReport.setVisibility(View.VISIBLE);
        }

        //Check Type Comment > sticker or text and set comment
        if (getType.equals("sticker")) {
            layoutEdit.setVisibility(View.GONE);
            textComments.setVisibility(View.INVISIBLE);
            stickerComment.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(getComment).into(stickerComment);
        } else {
            textComments.setText(getComment);
        }

        //Edit my Comment
        layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogEditComment = new BottomSheetDialog(CommentDetailActivity.this, R.style.BottomSheetDialog);
                View BottomSheetView = LayoutInflater.from(CommentDetailActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_edit_comment, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));

                loadingProgress = BottomSheetView.findViewById(R.id.progressBarComment);

                //Get detailComments and Set
                Edit_Comment = BottomSheetView.findViewById(R.id.Edit_Comment);
                Edit_Comment.getEditText().setText(getComment);

                //Edit Comment
                EditCommentButton = BottomSheetView.findViewById(R.id.CommentBtn);
                EditCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String EditComment = Edit_Comment.getEditText().getText().toString().trim();
                        updateComment(EditComment);
                    }
                });

                bottomSheetDialogEditComment.setContentView(BottomSheetView);
                bottomSheetDialogEditComment.show();


            }
        });

        //Delete my Comment
        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertBox();
            }
        });


    }



    //Function
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateComment(String editComment) {
        EditCommentButton.setVisibility(INVISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);

        DatabaseReference CommentRef = firebaseDatabase.getReference("Comment").child(postKey).child(CommentKey);

        //Update Comment and Tag
        CommentRef.child("contentComments").setValue(editComment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                textComments.setText(editComment);
                showMessage("แก้ไขความคิดเห็นเรียบร้อยแล้ว");
                bottomSheetDialogEditComment.dismiss();
            }
        });





    }

    //AlertBox
    private void AlertBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(CommentDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.confirm_delete_dialog, null);

        //Create AlertDialog
        AlertDialog builder = new AlertDialog.Builder(CommentDetailActivity.this)
                .setView(view)
                .create();

        //Click To Delete Post
        Button click_ok = view.findViewById(R.id.okButton);
        click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Close AlertDialog
                builder.dismiss();

                //int position = getAdapterPosition();
                //postKey = mData.get(position).getPostKey();

                //Remove Value Comment
                commentReference = FirebaseDatabase.getInstance().getReference("Comment");
                commentReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(postKey).child(CommentKey).exists()) {

                            commentReference.child(postKey).child(CommentKey).removeValue();
                            showMessage("ลบโพสต์เรียบร้อย");
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //Remove Value Like
                likeReference = FirebaseDatabase.getInstance().getReference("likeComment");
                likeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(CommentKey).exists()) {

                            likeReference.child(CommentKey).removeValue();

                        } else {

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });

        //Click To Cancel
        Button click_cancel = view.findViewById(R.id.cancelButton);
        click_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Close AlertDialog
                builder.dismiss();

            }
        });

        builder.show();

    }

    private String timestampToString(long time) {

        //ThaiBuddhistDate tbd = ThaiBuddhistDate.now(ZoneId.systemDefault());
        ThaiBuddhistDate tbd = ThaiBuddhistDate.now(ZoneId.systemDefault());
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);

        //String date = tbd.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String date = DateFormat.format("dd/MM/yyyy hh:mm", calendar).toString();

        return date;


    }

}