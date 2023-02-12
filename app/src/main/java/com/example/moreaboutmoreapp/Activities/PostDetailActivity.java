package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.moreaboutmoreapp.Adapters.StickerAdapter;
import com.example.moreaboutmoreapp.Models.Comment;
import com.example.moreaboutmoreapp.Models.Post;
import com.example.moreaboutmoreapp.Models.PushNotificationTask;
import com.example.moreaboutmoreapp.Models.Sticker;
import com.example.moreaboutmoreapp.Models.TokenStore;
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
    ImageView userProfile, Btn_BackPost, Btn_BackSticker;
    RelativeLayout layoutEdit, layoutDelete, layoutPin, layoutReport;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postReference, likeReference, commentReference;

    final LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetailActivity.this);
    RecyclerView commentRecyclerView, StickerRecyclerView;
    List<Comment> commentList;
    ArrayList<Sticker> stickerList;
    CommentAdapter commentAdapter;
    StickerAdapter stickerAdapter;
    private int lastPosition;

    String postKey;
    String uidReceiver, nickname, tokens; // for notification
    String fastNickName;
    String Count_comment;
    static String COMMENT_KEY = "Comment";
    BottomSheetDialog bottomSheetDialog, bottomSheetDialogEditPost, bottomSheetDialogSticker;

    Boolean testClick = false;
    private ProgressBar loadingProgress;
    private Button commentButton, commentBtnClick, stickerButton, MoreMenu, EditPostButton;
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

    SharedPreferences preferences;

    @Override
    protected void onPause() {
        super.onPause();
        //Save lastPosition RecyclerView onPause
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(PostDetailActivity.this);
        SharedPreferences.Editor dataPosition = getPrefs.edit();
        dataPosition.putInt("lastPosition", lastPosition);
        dataPosition.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Retrieve Last Position RecyclerView onResume
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(PostDetailActivity.this);
        lastPosition = getPrefs.getInt("lastPosition", 0);
    }

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

        fastNickName = getNickName();

        textTag = findViewById(R.id.textTag);
        userProfile = findViewById(R.id.userProfile);
        textUser = findViewById(R.id.textUser);
        textTime = findViewById(R.id.textTime);
        likeCount = findViewById(R.id.likeCount);
        textComments = findViewById(R.id.textComments);

        //Back To Home Activity
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

        /** Get And Set Data from postAdapter line at 480 */
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

        // get user id that receive notifiication
        uidReceiver = getIntent().getExtras().getString("uidReceiver");

        //Retrieve List Comment
        // commentRV stay in layout/activity_post_detail.xml > RecyclerView
        commentRecyclerView = findViewById(R.id.commentRV);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.scrollToPosition(lastPosition);
        //Get Position Scroll
        commentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();

            }
        });

        commentCount = findViewById(R.id.commentCount);

        //Check My Post for Button Check
        //Save My Post
        preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        if (getEmail.equals(firebaseUser.getDisplayName())) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("SaveMyPost", "Yes");
            editor.apply();
            //showMessage("Yes");

        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("SaveMyPost", "No");
            editor.apply();
            //showMessage("No");
        }


        //Save My PostKey form Post Detail Activity
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SavePostKey", postKey);
        editor.apply();


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

                }
                else {

                    bottomSheetDialog = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);
                    View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                            .inflate(R.layout.bottom_sheet_dialog_more_menu, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));

                    bottomSheetDialog.setContentView(BottomSheetView);
                    bottomSheetDialog.show();

//                    layoutPin = BottomSheetView.findViewById(R.id.layoutPin);
//                    layoutPin.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(getApplicationContext(), "ติดตามโพสต์นี้เรียบร้อย", Toast.LENGTH_SHORT).show();
//                            bottomSheetDialog.dismiss();
//                        }
//                    });
//
//                    layoutReport = BottomSheetView.findViewById(R.id.layoutReport);
//                    layoutReport.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(getApplicationContext(), "รายโพสต์นี้เรียบร้อย", Toast.LENGTH_SHORT).show();
//                            bottomSheetDialog.dismiss();
//                        }
//                    });

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

        //Button Add comment
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

        //Bottom Layout Button Add comment
        bottomSheetDialogSticker = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);
        RelativeLayout addComment = (RelativeLayout) findViewById(R.id.bottom_layerComment);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(PostDetailActivity.this, R.style.BottomSheetDialog);
                //bottomSheetDialog.setCancelable(false);

                View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_comment, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerComment));

                loadingProgress = BottomSheetView.findViewById(R.id.progressBarPost);
                commentButton = BottomSheetView.findViewById(R.id.commentButton);
                stickerButton = BottomSheetView.findViewById(R.id.stickerButton);
                detailComments = BottomSheetView.findViewById(R.id.detailComments);

                //addCommentBtn
                BottomSheetView.findViewById(R.id.commentButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commentButton.setVisibility(INVISIBLE);
                        stickerButton.setVisibility(INVISIBLE);
                        loadingProgress.setVisibility(View.VISIBLE);
                        // Add Text Comment Go To Firebase
                        addCommentBtn();
                    }
                });

                //addStickerBtn
                BottomSheetView.findViewById(R.id.stickerButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Close Bottom Sheet Add Comment
                        bottomSheetDialog.dismiss();


                        //bottomSheetDialogSticker.setCancelable(false);

                        View BottomSheetView = LayoutInflater.from(PostDetailActivity.this)
                                .inflate(R.layout.bottom_sheet_dialog_sticker, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerComment));

                        //Back to BottomSheet AddComment and Close BottomSheet Sticker
                        Btn_BackSticker = BottomSheetView.findViewById(R.id.Btn_BackSticker);
                        Btn_BackSticker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetDialog.show();
                                bottomSheetDialogSticker.dismiss();
                            }
                        });

                        //Get Sticker Form Firebase Storage
                        StickerRecyclerView = BottomSheetView.findViewById(R.id.StickerRV);
                        StickerRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));

                        stickerList = new ArrayList<>();

                        clearAll();
                        DatabaseReference stickerRef = firebaseDatabase.getReference().child("sticker");
                        stickerRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                clearAll();

                                for ( DataSnapshot snap: snapshot.getChildren()) {

                                    Sticker sticker = new Sticker();
                                    sticker.setStickerUrl(snap.getValue().toString());

                                    stickerList.add(sticker);
                                }
                                // Declare SharePreference
                                SharedPreferences sharedPreferences = getSharedPreferences("uidReceiver", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("uid", uidReceiver);
                                String nickname = getNickName();
                                Log.d("GET NICKNAME", "onDataChange: " + nickname);
                                editor.putString("nickname", nickname);
                                editor.apply();

                                stickerAdapter = new StickerAdapter(getApplicationContext(), stickerList);
                                StickerRecyclerView.setAdapter(stickerAdapter);
                                stickerAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(PostDetailActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
                            }
                        });

                        bottomSheetDialogSticker.setContentView(BottomSheetView);
                        bottomSheetDialogSticker.show();

                    }
                });

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();
            }
        });

        //init RecyclerView Like And Comment Count
        initRvLikeComment();

    } // end of onCreate




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

                if (bottomSheetDialogSticker.isShowing()) {
                    bottomSheetDialogSticker.dismiss();
                    commentRecyclerView.scrollToPosition(commentList.size() - 1 );
                }

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
            stickerButton.setVisibility(View.VISIBLE);
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
    }

    private void loadImageNameUser(String image, String name) {
        //Comment comment = new Comment()
        Comment comment = new Comment(detailComments.getEditText().getText().toString(), firebaseUser.getUid(), firebaseUser.getDisplayName(), name, image,"text", "false");

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
                stickerButton.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(INVISIBLE);
                bottomSheetDialog.dismiss();
                commentRecyclerView.scrollToPosition(commentList.size() - 1 );

                // notification
                /** สงสัยจังว่า post id จะช่วย navigation เมื่อ user tap ที่แจ้งเตือนได้รึปล่าวน๊า */
                String post_id = postKey;
                /** Push notification */
                pushNotification();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to added comment : " +e.getMessage());
            }
        });

    }

    private String getNickName() {

        String uid = firebaseAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userData").child(uid).child("name");
        System.out.println(databaseReference);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickname = snapshot.getValue().toString();
                //Toast.makeText(mContext, nickname, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("get nick name", "onCancelled: " + error);
            }
        });
        //Log.d("CHECK NICKNAME", "getNickName: " + nickname);
        return nickname;
    }


    private void pushNotification() {
        String uid = uidReceiver; // get receiver_uid
        String user_nickname = getNickName();
        int check_error_on_fetching_name = 0;
        do {
            user_nickname = getNickName();
        } while (!(user_nickname.isEmpty()));
//        if (user_nickname.isEmpty()) {
//            check_error_on_fetching_name++;
//
//            if (user_nickname.isEmpty()) {
//                check_error_on_fetching_name++;
//                user_nickname = getNickName();
//            }
//            Log.d("Checking error on fetch name", "pushNotification: " + check_error_on_fetching_name);
//        }
        String finalUser_nickname = user_nickname;
        String type = "post moment"; // identify type notification
        String path = "tokens/" + uid + "/";
        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference(path);
        tokenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // tokens = snapshot.getValue().toString();
                TokenStore tokenStore = snapshot.getValue(TokenStore.class);
                tokens = tokenStore.token;
                Log.d("Fetch Token", "Tokens owner post is " + tokens);

                // get uid receiver is mean owner content that you make event noty happen
                String uidReceiver = uid;

                // get post key here


                // if an pusher and receiver notification is a same user
                // notification should not show on display
                if (FirebaseAuth.getInstance().getUid().equals(uidReceiver)) {
                    // Not do anything
                } else {
                    PushNotificationTask pushNotificationTask = new PushNotificationTask();
                    pushNotificationTask.execute(tokens, finalUser_nickname, type, uidReceiver, postKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Fetch Token", "Error to fetching tokens because " + error);
            }
        }); // end of call Real time DB in tokens
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
                commentRecyclerView.scrollToPosition(lastPosition);

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

    private void clearAll() {

        if (stickerList != null) {

            stickerList.clear();

            if (stickerAdapter != null) {
                stickerAdapter.notifyDataSetChanged();
            }

        }

        stickerList = new ArrayList<>();

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