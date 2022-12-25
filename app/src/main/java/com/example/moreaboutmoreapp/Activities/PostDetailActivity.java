package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.moreaboutmoreapp.Adapters.CommentAdapter;
import com.example.moreaboutmoreapp.Adapters.PostAdapter;
import com.example.moreaboutmoreapp.Models.Comment;
import com.example.moreaboutmoreapp.Models.Post;
import com.example.moreaboutmoreapp.R;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class PostDetailActivity extends AppCompatActivity {

    TextView textTag, textTime, textUser, textComments, likeCount, dislikeCount, commentCount;
    ImageView userProfile, Btn_BackPost;
    RelativeLayout layoutEdit, layoutDelete, layoutPin, layoutReport;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postReference, likeReference, commentReference;
    List<Comment> commentList;
    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;

    String postKey;
    String Count_comment;
    static String COMMENT_KEY = "Comment";
    BottomSheetDialog bottomSheetDialog, bottomSheetDialogEditPost;

    Boolean testClick = false;
    private ProgressBar loadingProgress;
    private Button commentButton, commentBtnClick, MoreMenu, EditPostButton;
    private ToggleButton likeBtn;
    private TextInputLayout detailComments;

    TextInputLayout Edit_Post;
    TextInputLayout EditSelectTag;

    AutoCompleteTextView Edit_Tag;
    ArrayAdapter<String> adapterItem;
    String itemSelectTag = "";
    String[] EditTags = {"ปรึกษาการเรียน",
            "ลงทะเบียนเรียน",
            "หาเพื่อนทำงานกลุ่ม",
            "หาเพื่อนติวหนังสือ",
            "หาเพื่อนใหม่",
            "คุยเล่น",
            "อื่น ๆ"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

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

        textTag = findViewById(R.id.textTag);
        userProfile = findViewById(R.id.userProfile);
        textUser = findViewById(R.id.textUser);
        textTime = findViewById(R.id.textTime);
        likeCount = findViewById(R.id.likeCount);
        textComments = findViewById(R.id.textComments);

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
        String getTag = getIntent().getExtras().getString("textTag");
        textTag.setText(getTag);

        String getEmail = getIntent().getExtras().getString("textEmail");

        String getPhoto = getIntent().getExtras().getString("userPhotoImg");
        Picasso.get().load(getPhoto).into(userProfile);

        String getUser = getIntent().getExtras().getString("textUser");
        textUser.setText(getUser);

        String getComment = getIntent().getExtras().getString("textComments");
        textComments.setText(getComment);

        postKey = getIntent().getExtras().getString("postKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        textTime.setText(date);

        //Retrieve List Comment
        // commentRV stay in layout/activity_post_detail.xml > RecyclerView
        commentRecyclerView = findViewById(R.id.commentRV);
        commentCount = findViewById(R.id.commentCount);

        //MoreMenu
        MoreMenu = findViewById(R.id.MoreMenu);
        MoreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getEmail.equals(firebaseUser.getDisplayName())) {

                    bottomSheetDialog = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);
                    View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                            .inflate(R.layout.bottom_sheet_dialog_more_menu, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));

                    //setVisibility Gone
                    layoutPin = BottomSheetView.findViewById(R.id.layoutPin);
                    layoutReport = BottomSheetView.findViewById(R.id.layoutReport);
                    layoutPin.setVisibility(View.GONE);
                    layoutReport.setVisibility(View.GONE);

                    //Edit Post
                    layoutEdit = BottomSheetView.findViewById(R.id.layoutEdit);
                    layoutEdit.setVisibility(View.VISIBLE);
                    layoutEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            bottomSheetDialogEditPost = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);
                            View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                                    .inflate(R.layout.bottom_sheet_dialog_edit_post, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));

                            loadingProgress = BottomSheetView.findViewById(R.id.progressBarPost);
                            EditPostButton = BottomSheetView.findViewById(R.id.postBtn);

                            //Get detailComments and Set
                            Edit_Post = BottomSheetView.findViewById(R.id.Edit_Post);
                            DatabaseReference userDataRef = firebaseDatabase.getReference("allPost").child(postKey);
                            DatabaseReference getCommentRef = userDataRef.child("detailComments");
                            getCommentRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        String Get_Comment = snapshot.getValue().toString();
                                        Edit_Post.getEditText().setText(Get_Comment);
                                        textComments.setText(Get_Comment);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //Get Tag and Set
                            EditSelectTag = BottomSheetView.findViewById(R.id.EditSelectTag);
                            Edit_Tag = BottomSheetView.findViewById(R.id.Edit_Tag);
                            adapterItem = new ArrayAdapter<String>(PostDetailActivity.this, R.layout.list_item_tag, EditTags);
                            Edit_Tag.setAdapter(adapterItem);
                            Edit_Tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    itemSelectTag = adapterView.getItemAtPosition(i).toString();
                                    //Toast.makeText(mContext, "Click : " + itemSelectTag, Toast.LENGTH_SHORT).show();
                                }
                            });

                            //Save Edit Post and Tag
                            EditPostButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String updateComment = Edit_Post.getEditText().getText().toString().trim();
                                    updateCommentTag(updateComment, itemSelectTag);

                                    if (itemSelectTag.isEmpty()) {
                                        textTag.setText(getTag);
                                    } else {
                                        textTag.setText(itemSelectTag);
                                    }

                                }
                            });

                            bottomSheetDialogEditPost.setContentView(BottomSheetView);
                            bottomSheetDialogEditPost.show();

                            bottomSheetDialog.dismiss();

                        }
                    });

                    //Delete Post
                    layoutDelete = BottomSheetView.findViewById(R.id.layoutDelete);
                    layoutDelete.setVisibility(View.VISIBLE);
                    layoutDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertBox();
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bottomSheetDialog.setContentView(BottomSheetView);
                    bottomSheetDialog.show();

                } else {

                    bottomSheetDialog = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);
                    View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                            .inflate(R.layout.bottom_sheet_dialog_more_menu, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));


                    bottomSheetDialog.setContentView(BottomSheetView);
                    bottomSheetDialog.show();

                }
            }
        });

        //Button Like
        likeBtn = findViewById(R.id.likeBtn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testClick = true;

                likeReference = FirebaseDatabase.getInstance().getReference("likes");
                likeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (testClick == true) {

                            if (snapshot.child(postKey).hasChild(firebaseUser.getUid())) {

                                //Remove Value Like
                                likeReference.child(postKey).child(firebaseUser.getUid()).removeValue();
                                testClick = false;

                            } else {

                                //Set Value Like
                                likeReference.child(postKey).child(firebaseUser.getUid()).setValue("true");
                                testClick = false;

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //addComment
        commentBtnClick = findViewById(R.id.commentBtn);
        commentBtnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);

                View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_comment, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerComment));

                loadingProgress = BottomSheetView.findViewById(R.id.progressBarPost);
                commentButton = BottomSheetView.findViewById(R.id.commentButton);
                detailComments = BottomSheetView.findViewById(R.id.detailComments);

                //addCommentBtn
                BottomSheetView.findViewById(R.id.commentButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commentButton.setVisibility(INVISIBLE);
                        loadingProgress.setVisibility(View.VISIBLE);

                        // Add All Data Go To Firebase
                        addCommentBtn();


                    }
                });

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();

            }
        });

        RelativeLayout addComment = (RelativeLayout) findViewById(R.id.bottom_layerComment);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);

                View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_comment, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerComment));

                loadingProgress = BottomSheetView.findViewById(R.id.progressBarPost);
                commentButton = BottomSheetView.findViewById(R.id.commentButton);
                detailComments = BottomSheetView.findViewById(R.id.detailComments);

                //addCommentBtn
                BottomSheetView.findViewById(R.id.commentButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commentButton.setVisibility(INVISIBLE);
                        loadingProgress.setVisibility(View.VISIBLE);

                        // Add All Data Go To Firebase
                        addCommentBtn();


                    }
                });

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();
            }
        });


        //init RecyclerView Like And Comment Count
        initRvLikeComment();

    }


    //Function
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initRvLikeComment() {

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Count_comment = snapshot.getChildrenCount() + "";
                commentCount.setText(Count_comment);
                //showMessage(Count);

                commentList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                commentRecyclerView.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get Like Count
        DatabaseReference LikeRef = firebaseDatabase.getReference("likes").child(postKey);
        DatabaseReference checkLikeRef = LikeRef.child(firebaseUser.getUid());

        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    likeCount.setText(snapshot.getChildrenCount()+"");
                } else {
                    likeCount.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkLikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    likeBtn.setChecked(true);
                } else {
                    likeBtn.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private boolean validateComment() {
        String detailInput = detailComments.getEditText().getText().toString().trim();

        if (detailInput.isEmpty()) {
            detailComments.setError("Field can't be empty");
            commentButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            detailComments.setError(null);
            return true;
        }
    }

    private void addCommentBtn() {
        if (!validateComment()) {
            return;
        } else {
            //Get_Uri Profile
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
    }

    private void loadImageNameUser(String image, String name) {
        Comment comment = new Comment(detailComments.getEditText().getText().toString(), firebaseUser.getUid(), firebaseUser.getDisplayName(), name, image);

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
                loadingProgress.setVisibility(INVISIBLE);
                bottomSheetDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to added comment : " +e.getMessage());
            }
        });

    }

    private boolean validateSelectTag() {
        String tagInput = Edit_Tag.getText().toString().trim();

        if (tagInput.isEmpty()) {
            EditSelectTag.setError("Required");
            EditPostButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            EditSelectTag.setError(null);
            return true;
        }
    }

    private void updateCommentTag(String updateComment, String itemSelectTag) {

        if (!validateSelectTag()) {
            return;
        } else {
            EditPostButton.setVisibility(INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);

            DatabaseReference userDataRef = firebaseDatabase.getReference("allPost").child(postKey);

            //Update Comment and Tag
            userDataRef.child("detailComments").setValue(updateComment);
            userDataRef.child("selectTag").setValue(itemSelectTag);

            showMessage("แก้ไขโพสต์เรียบร้อยแล้ว");
            bottomSheetDialogEditPost.dismiss();

        }

    }

    //AlertBox
    private void AlertBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(PostDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.confirm_delete_dialog, null);

        //Create AlertDialog
        AlertDialog builder = new AlertDialog.Builder(PostDetailActivity.this)
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

                //Remove Value Post
                postReference = FirebaseDatabase.getInstance().getReference("allPost");
                postReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(postKey).exists()) {

                            postReference.child(postKey).removeValue();

                        } else {
                            //Toast.makeText(mContext, "เกิดข้อผิดพลาด ลองอีกครั้ง", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Remove Value Like
                likeReference = FirebaseDatabase.getInstance().getReference("likes");
                likeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(postKey).exists()) {

                            likeReference.child(postKey).removeValue();

                        } else {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Remove Value Comment
                commentReference = FirebaseDatabase.getInstance().getReference("Comment");
                commentReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(postKey).exists()) {

                            commentReference.child(postKey).removeValue();

                        } else {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                showMessage("ลบโพสต์เรียบร้อย");
                finish();


            }
        });

        //Click To Cancel
        Button click_cancel = view.findViewById(R.id.cancelButton);
        click_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Close AlertDialog
                builder.dismiss();
                bottomSheetDialog.show();

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