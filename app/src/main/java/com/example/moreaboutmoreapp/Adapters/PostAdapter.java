package com.example.moreaboutmoreapp.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.INVISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreaboutmoreapp.Activities.PostDetailActivity;
import com.example.moreaboutmoreapp.HomeFragment;
import com.example.moreaboutmoreapp.Models.PinnerData;
import com.example.moreaboutmoreapp.Models.Post;
import com.example.moreaboutmoreapp.Models.PushNotificationTask;
import com.example.moreaboutmoreapp.Models.TokenStore;
import com.example.moreaboutmoreapp.R;
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

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;
    String postKey, postEditKey, postKeyLike, userID;
    DatabaseReference postReference, likeReference, commentReference, tokenReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    public static TextView showStatusText;

    // For notification
    // Not used NAJA NotificationClass notificationClass = NotificationClass.getInstance();

    // for tag reset layout
    public static String changeLayout = "";

    public static String tokens;
    String nickname;

    // for reset tag
    public static ArrayList<Post> filterResetTag;


    //CardView CV_Row_Post;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {

        FirebaseDatabase firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Set Tag
        holder.textTag.setText(mData.get(position).getSelectTag());

        //Set Img Profile
        Picasso.get().load(mData.get(position).getUserPhoto()).into(holder.imgUser);

        //Set Name
        // > GET b
        String Email = mData.get(position).getUserName().substring(0, 1);
        // > b to B
        String toUppercase = Email.toUpperCase();
        // > B6x - NickName
        String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
        holder.textUser.setText(ID_NAME);

        //Set Time
        holder.textTime.setText(timestampToString((Long)mData.get(position).getTimeStamp()));

        //Set Comment
        String comments = mData.get(position).getDetailComments();
         /*if (holder.textComments.getLineCount() > 3){
            //comments = comments.substring(0,20) + " ...";

        }*/
       if (comments.length() > 155) {
           comments = comments.substring(0,155) + " ...";
           holder.textPost.setText(comments);
           holder.textViewMore.setVisibility(View.VISIBLE);

           /*holder.textViewMore.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Toast.makeText(mContext, "Click", Toast.LENGTH_SHORT).show();
               }
           });*/

        } else {
           holder.textViewMore.setVisibility(View.INVISIBLE);
           holder.textPost.setText(comments);
       }

       //Check Our Post
       if (mData.get(position).getUserName().equals(firebaseUser.getDisplayName())){

           //holder.textUser.setTextColor(ContextCompat.getColor(mContext, R.color.nav_color));

       } else {
           //CV_Row_Post.setVisibility(View.GONE);
       }

        //Get Comment Count
        DatabaseReference commentRef = firebaseDatabase.getReference("Comment").child(mData.get(position).getPostKey());
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    holder.commentCount.setText(snapshot.getChildrenCount()+"");
                } else {
                    holder.commentCount.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Get Like Count
        DatabaseReference LikeRef = firebaseDatabase.getReference("likes").child(mData.get(position).getPostKey());
        DatabaseReference checkLikeRef = LikeRef.child(firebaseUser.getUid());

        //Set Like Count
        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    holder.textCountLike.setText(snapshot.getChildrenCount()+"");
                } else {
                    holder.textCountLike.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Set Check Like Button
        checkLikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    holder.likeBtn.setChecked(true);
                } else {
                    holder.likeBtn.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void filterList(ArrayList<Post> filterlist) {
        mData = filterlist;
        if (mData.isEmpty()) {
            Toast.makeText(mContext, "ไม่มีโพสต์ที่ท่านกำลังค้นหาอยู่ในขณะนี้", Toast.LENGTH_LONG).show();
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        FirebaseDatabase db;

        ImageView imgUser;
        TextView textTag, textUser, textTime, textPost, textViewMore, textCountLike, textCountDisLike, commentCount;

        BottomSheetDialog bottomSheetDialog, bottomSheetDialogEditPost;

        RelativeLayout layoutEdit, layoutDelete, layoutPin, layoutReport, layoutTag;

        Boolean testClick = false;
        Button commentBtn, MoreMenu, EditPostButton;
        ToggleButton likeBtn, dislikeBtn;
        ProgressBar loadingProgress;

        TextInputLayout Edit_Post;
        TextInputLayout EditSelectTag;

        AutoCompleteTextView Edit_Tag;
        ArrayAdapter<String> adapterItem;
        String itemSelectTag;
        String[] EditTags = {"ปรึกษาการเรียน",
                "ลงทะเบียนเรียน",
                "หาเพื่อนทำงานกลุ่ม",
                "หาเพื่อนติวหนังสือ",
                "หาเพื่อนใหม่",
                "คุยเล่น",
                "อื่น ๆ"};



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //CV_Row_Post = itemView.findViewById(R.id.CV_Row_Post);
             filterResetTag = new ArrayList<>();

            db = FirebaseDatabase.getInstance();

            // for reset tag search
            showStatusText = itemView.findViewById(R.id.tagStatus);

            textTag = itemView.findViewById(R.id.textPosts);
            imgUser = itemView.findViewById(R.id.userProfile);
            textUser = itemView.findViewById(R.id.textUser);
            textPost = itemView.findViewById(R.id.textPost);
            textTime = itemView.findViewById(R.id.textTime);

            commentCount = itemView.findViewById(R.id.commentCount);

            textViewMore = itemView.findViewById(R.id.textViewMore);
            textCountLike = itemView.findViewById(R.id.likeCount);

            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);

            // for searching tag with tap layout
            layoutTag = itemView.findViewById(R.id.layoutTag);
            layoutTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = textTag.getText().toString();
                    filterTag(tag);
                }

                private void filterTag(String tag) {
                    ArrayList<Post> filterTag = new ArrayList<>();

                    for (Post item : mData) {
                        // we check text input by user matching content in any post
                        // with "contains()" method
                        if (item.getSelectTag().toLowerCase().contains(tag.toLowerCase())) {
                            filterTag.add(item);
                        }
                        filterResetTag.add(item);
                    }

                    // showing textview
                    int position = getAdapterPosition();
                    showStatusText.setText("Tag : " + mData.get(position).getSelectTag());
                    showStatusText.setVisibility(View.VISIBLE);

//                    Bundle bundle = new Bundle();
//                    changeLayout = "changeLayout";
//                    bundle.putString("message", changeLayout);
//
//                    // Set Fragmentclass Arguments
//                    HomeFragment fragobj = new HomeFragment();
//                    fragobj.setArguments(bundle);

                    filterList(filterTag);

                }
            });

//             unit test in reset tag selected
            showStatusText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterList(filterResetTag);
                    showStatusText.setVisibility(View.GONE);
                }
            });

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });*/

            //Button MoreMenu
            MoreMenu = itemView.findViewById(R.id.MoreMenu);
            MoreMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int position = getAdapterPosition();
                        postEditKey = mData.get(position).getPostKey();

                        if (mData.get(position).getUserName().equals(firebaseUser.getDisplayName())){

                            bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialog);
                            View BottomSheetView = LayoutInflater.from(mContext)
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

                                    bottomSheetDialogEditPost = new BottomSheetDialog(mContext, R.style.BottomSheetDialog);
                                    View BottomSheetView = LayoutInflater.from(mContext)
                                            .inflate(R.layout.bottom_sheet_dialog_edit_post, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));

                                    loadingProgress = BottomSheetView.findViewById(R.id.progressBarPost);
                                    EditPostButton = BottomSheetView.findViewById(R.id.postBtn);

                                    //Get detailComments and Set
                                    Edit_Post = BottomSheetView.findViewById(R.id.Edit_Post);
                                    DatabaseReference userDataRef = db.getReference("allPost").child(postEditKey);
                                    DatabaseReference getCommentRef = userDataRef.child("detailComments");
                                    getCommentRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                String Get_Comment = snapshot.getValue().toString();
                                                Edit_Post.getEditText().setText(Get_Comment);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    //Get Tag and Set
                                    EditSelectTag = BottomSheetView.findViewById(R.id.EditSelectTag);
                                    Edit_Tag = BottomSheetView.findViewById(R.id.Edit_Tag);
                                    adapterItem = new ArrayAdapter<String>(mContext,R.layout.list_item_tag,EditTags);
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
                            bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialog);
                            View BottomSheetView = LayoutInflater.from(mContext)
                                    .inflate(R.layout.bottom_sheet_dialog_more_menu, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));

                            bottomSheetDialog.setContentView(BottomSheetView);
                            bottomSheetDialog.show();

                            userID = FirebaseAuth.getInstance().getUid();

                            // Pin post process
                            layoutPin = BottomSheetView.findViewById(R.id.layoutPin);
                            layoutPin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // get token
                                    // Notification to owner post
                                    String type = "pin post";
                                    String path = "tokens/" + userID + "/";
                                    tokenReference = FirebaseDatabase.getInstance().getReference(path);
                                    tokenReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // tokens = snapshot.getValue().toString();
                                            TokenStore tokenStore = snapshot.getValue(TokenStore.class);
                                            tokens = tokenStore.token;
                                            Log.d("Fetch Token", "Tokens owner post is " + tokens);

                                            // get uid receiver is mean owner content that you make event noty happen
                                            String uidReceiver = mData.get(position).getUserId();

                                            // We need to create database to store pin request user
                                            // When We have this we need notice user when target pin post have been comment
                                            // We need get a token pinner, uid pinner, post id to verify that post id is correct 2 side

                                            String uid = FirebaseAuth.getInstance().getUid();
                                            String postId = mData.get(position).getPostKey();

                                            PinnerData pinnerData = new PinnerData(uid,postId,tokens);

                                            DatabaseReference PinnerRequest = FirebaseDatabase.getInstance().getReference("PinnerRequest").child(FirebaseAuth.getInstance().getUid()).child(mData.get(position).getPostKey());

                                            PinnerRequest.setValue(pinnerData);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.d("Fetch Token", "Error to fetching tokens because " + error);
                                        }
                                    }); // end of call Real time DB in tokens


                                    Toast.makeText(mContext.getApplicationContext(), "ติดตามโพสต์นี้เรียบร้อย", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();
                                }
                            });

                            layoutReport = BottomSheetView.findViewById(R.id.layoutReport);
                            layoutReport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext.getApplicationContext(), "รายโพสต์นี้เรียบร้อย", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();
                                }
                            });

                        }
                    }
                });

            //Button Like
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    testClick = true;
                    
                    int position = getAdapterPosition();
                    postKeyLike = mData.get(position).getPostKey();
                    userID = mData.get(position).getUserId();

                    // get nickname from firebase

//                    do {
//                        nickname = getNickName();
//                        Log.d("Check nickname", "onCancelled: " + nickname);
//                    } while (nickname == null);




                    likeReference = FirebaseDatabase.getInstance().getReference("likes");
                    likeReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (testClick == true) {

                                if (snapshot.child(postKeyLike).hasChild(firebaseUser.getUid())) {

                                    //Remove Value Like
                                    likeReference.child(postKeyLike).child(firebaseUser.getUid()).removeValue();
                                    testClick = false;

                                    String nickname = getNickName();

                                } else {
                                    String nickname;
                                    nickname = getNickName();
                                    // Log.d("Check nickname", "onCancelled: " + nickname);

                                    SharedPreferences pref = mContext.getSharedPreferences("nicknameFirebase",MODE_PRIVATE);
                                    String finalNickname = pref.getString("names", getNickName());
                                    Log.d("Check nickname", "final nickname: " + finalNickname);
                                    // set type notification
                                    String type = "like post";

                                    //Set Value Like
                                    likeReference.child(postKeyLike).child(firebaseUser.getUid()).setValue("true");
                                    testClick = false;

                                    // Notification to owner post
                                    String path = "tokens/" + userID + "/";
                                    tokenReference = FirebaseDatabase.getInstance().getReference(path);
                                    tokenReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // tokens = snapshot.getValue().toString();
                                            TokenStore tokenStore = snapshot.getValue(TokenStore.class);
                                            tokens = tokenStore.token;
                                            Log.d("Fetch Token", "Tokens owner post is " + tokens);

                                            // get uid receiver is mean owner content that you make event noty happen
                                            String uidReceiver = mData.get(position).getUserId();

                                            // get post key
                                            String postKey = mData.get(position).getPostKey();

                                            // if an pusher and receiver notification is a same user
                                            // notification should not show on display
                                            if (FirebaseAuth.getInstance().getUid().equals(uidReceiver)) {
                                                // Not do anything
                                            } else {
                                                PushNotificationTask pushNotificationTask = new PushNotificationTask();
                                                pushNotificationTask.execute(tokens, finalNickname, type, uidReceiver, postKey);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.d("Fetch Token", "Error to fetching tokens because " + error);
                                        }
                                    }); // end of call Real time DB in tokens

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                public String getNickName() {
                    String uid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userData").child(uid).child("name");
                    //System.out.println(databaseReference);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            nickname = snapshot.getValue().toString();
                            SharedPreferences pref = mContext.getSharedPreferences("nicknameFirebase", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("names", nickname);
                            editor.apply();
                            Toast.makeText(mContext, pref.getString("names","OMG"), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("get nick name", "onCancelled: " + error);
                        }
                    });
                    Log.d("CHECK NICKNAME", "getNickName: " + nickname);
                    return nickname;
                }
            });

            //Button Open PostDetail Activity
            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("textTag", mData.get(position).getSelectTag());
                    postDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());
                    postDetailActivity.putExtra("textComments", mData.get(position).getDetailComments());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    /** send owner post uid to identify who should receive notification when someone comment there post.*/
                    postDetailActivity.putExtra("uidReceiver", mData.get(position).getUserId());

                    // > GET b
                    String Email = mData.get(position).getUserName().substring(0, 1);
                    // > b to B
                    String toUppercase = Email.toUpperCase();
                    // > B6x - NickName
                    String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
                    postDetailActivity.putExtra("textUser",ID_NAME);

                    // putExtra Full Email
                    postDetailActivity.putExtra("textEmail", mData.get(position).getUserName());

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });

            //Button View More
            textViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("textTag", mData.get(position).getSelectTag());
                    postDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());
                    postDetailActivity.putExtra("textComments", mData.get(position).getDetailComments());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());

                    // > GET b
                    String Email = mData.get(position).getUserName().substring(0, 1);
                    // > b to B
                    String toUppercase = Email.toUpperCase();
                    // > B6x - NickName
                    String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
                    postDetailActivity.putExtra("textUser", ID_NAME);

                    // putExtra Full Email
                    postDetailActivity.putExtra("textEmail", mData.get(position).getUserName());

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    mContext.startActivity(postDetailActivity);
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

        //AlertBox
        private void AlertBox() {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.confirm_delete_dialog, null);

            //Create AlertDialog
            AlertDialog builder = new AlertDialog.Builder(mContext)
                    .setView(view)
                    .create();

            //Click To Delete Post
            Button click_ok = view.findViewById(R.id.okButton);
            click_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Close AlertDialog
                    builder.dismiss();

                    int position = getAdapterPosition();
                    postKey = mData.get(position).getPostKey();

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

        private void updateCommentTag(String updateComment, String itemSelectTag) {

            if (!validateSelectTag()) {
                return;
            } else {
                EditPostButton.setVisibility(INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                DatabaseReference userDataRef = db.getReference("allPost").child(postEditKey);

                //Update Comment and Tag
                userDataRef.child("detailComments").setValue(updateComment);
                userDataRef.child("selectTag").setValue(itemSelectTag);

                bottomSheetDialogEditPost.dismiss();

            }

        }


    }




    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS;
    private static final int YEAR_MILLIS = 365 * WEEK_MILLIS;

    private String timestampToString(long time) {

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "เมื่อสักครู่";
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "เมื่อสักครู่";

        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 นาที";

        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " นาที";

        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 ชม.";

        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " ชม.";

        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 วัน";

        } else if (diff < 7 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " วัน";

        } else if (diff < 2 * WEEK_MILLIS) {
            return "1 สัปดาห์";

        } else {
            return diff / WEEK_MILLIS + " สัปดาห์";
        }


    }



}
