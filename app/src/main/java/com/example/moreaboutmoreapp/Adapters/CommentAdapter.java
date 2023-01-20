package com.example.moreaboutmoreapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moreaboutmoreapp.Activities.CommentDetailActivity;
import com.example.moreaboutmoreapp.Activities.FirstUserActivity;
import com.example.moreaboutmoreapp.Activities.LoginActivity;
import com.example.moreaboutmoreapp.Activities.PostDetailActivity;
import com.example.moreaboutmoreapp.Activities.ProfileActivity;
import com.example.moreaboutmoreapp.Models.Comment;
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

import java.time.ZoneId;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;
    String FullEmail;
    String commentKey, commentEditKey, commentKeyLike, commentCheckTrue, userID;
    DatabaseReference commentLikeReference, likeReference, commentReference, trueReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    Boolean testClick = false;
    Boolean checkClick = false;
    SharedPreferences preferences;

    // Function Constructor 002
    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    // >>> END Constructor <<< //

    // Function Implement Methods
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        FirebaseDatabase firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Set Profile image
        Picasso.get().load(mData.get(position).getUserPhoto()).into(holder.userProfileComment);

        //Set Time ago
        holder.textTime.setText(timestampToString((Long)mData.get(position).getTimeStamp()));

        //Check Comment > Text or Url and Set
        if(mData.get(position).getCommentType().equals("sticker")) {

            holder.textComment.setVisibility(View.INVISIBLE);
            holder.stickerComment.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(mData.get(position).getContentComments()).into(holder.stickerComment);

        } else {

            holder.textComment.setText(mData.get(position).getContentComments());

        }


        //Set Name
        // > GET b
        String Email = mData.get(position).getUserName().substring(0, 1);
        // > b to B
        String toUppercase = Email.toUpperCase();
        // > B6x - NickName
        String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
        holder.textUser.setText(ID_NAME);

        FullEmail = mData.get(position).getUserName();

        //Check Button Check True
        if (mData.get(position).getUserName().equals(firebaseUser.getDisplayName())){

            //holder.textUser.setTextColor(ContextCompat.getColor(mContext, R.color.nav_color));
            //holder.trueBtn.setVisibility(View.INVISIBLE);
        } else {

        }

        String KeyTrue = preferences.getString("SaveKeyComment", "");


        if (mData.get(position).getCommentKey().equals(KeyTrue)){
            holder.trueBtn.setVisibility(View.VISIBLE);

        } else if (KeyTrue.isEmpty()) {
            holder.trueBtn.setVisibility(View.VISIBLE);
            //Toast.makeText(mContext, "NotKeyTrue", Toast.LENGTH_SHORT).show();
        }
        else {
            holder.trueBtn.setVisibility(View.INVISIBLE);
        }

        //if (mData.get(position).getCommentKey().equals(KeyTrue)) {
          //
        //}

        //Get Like Count
        DatabaseReference LikeRef = firebaseDatabase.getReference("likeComment").child(mData.get(position).getCommentKey());
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
            }//;;

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Check True Button on Firebase
        DatabaseReference TureRef = firebaseDatabase.getReference("commentCheckTrue").child(mData.get(position).getCommentKey());
        DatabaseReference checkTureRef = TureRef.child(firebaseUser.getUid());

        //Set Check True Button
        checkTureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    holder.IconCheckTrue.setVisibility(View.VISIBLE);
                    holder.textUser.setTextColor(ContextCompat.getColor(mContext, R.color.nav_color));
                    holder.trueBtn.setChecked(true);
                } else {
                    holder.IconCheckTrue.setVisibility(View.INVISIBLE);
                    holder.textUser.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    holder.trueBtn.setChecked(false);
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
    // >>> END Implement Methods <<< //


    // Function Constructor 001
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        FirebaseDatabase db;
        ImageView userProfileComment, IconCheckTrue;
        GifImageView stickerComment;
        TextView textUser, textTime, textComment, textCountLike, textCountDisLike;

        BottomSheetDialog bottomSheetDialogEditComment;
        Button  MoreMenu, EditCommentButton;
        ToggleButton likeBtn, trueBtn;
        ProgressBar loadingProgress;

        TextInputLayout Edit_Comment;

        RelativeLayout layoutEdit, layoutDelete, layoutPin, layoutReport;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            db = FirebaseDatabase.getInstance();

            textUser = itemView.findViewById(R.id.textUser);
            textTime = itemView.findViewById(R.id.textTime);
            textComment = itemView.findViewById(R.id.textComment);
            stickerComment = itemView.findViewById(R.id.stickerComment);
            textCountLike = itemView.findViewById(R.id.likeCount);
            userProfileComment = itemView.findViewById(R.id.userProfileComment);


            //Button MoreMenu
            MoreMenu = itemView.findViewById(R.id.MoreMenu);
            MoreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    commentEditKey = mData.get(position).getPostKey();
                    //Toast.makeText(mContext, commentEditKey+"", Toast.LENGTH_SHORT).show();

                    // Open Comment Detail Activity
                    if (mData.get(position).getUserName().equals(firebaseUser.getDisplayName())){

                        //Toast.makeText(mContext, "Coming Soon...", Toast.LENGTH_SHORT).show();

                        Intent CommentDetailActivity = new Intent(mContext, CommentDetailActivity.class);
                        CommentDetailActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //Put Data

                        CommentDetailActivity.putExtra("CommentKey", mData.get(position).getCommentKey());

                        CommentDetailActivity.putExtra("postKey", mData.get(position).getPostKey());

                        CommentDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());

                        // > GET b
                        String Email = mData.get(position).getUserName().substring(0, 1);
                        // > b to B
                        String toUppercase = Email.toUpperCase();
                        // > B6x - NickName
                        String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
                        CommentDetailActivity.putExtra("textUser",ID_NAME);

                        CommentDetailActivity.putExtra("textEmail", mData.get(position).getUserName());

                        long timestamp = (long) mData.get(position).getTimeStamp();
                        CommentDetailActivity.putExtra("postDate", timestamp);

                        CommentDetailActivity.putExtra("textComments", mData.get(position).getContentComments());

                        CommentDetailActivity.putExtra("typeComments", mData.get(position).getCommentType());



                        //CommentDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());
                        mContext.startActivity(CommentDetailActivity);

                    } else {

                        Intent CommentDetailActivity = new Intent(mContext, CommentDetailActivity.class);
                        CommentDetailActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //Put Data

                        CommentDetailActivity.putExtra("CommentKey", mData.get(position).getCommentKey());

                        CommentDetailActivity.putExtra("postKey", mData.get(position).getPostKey());

                        CommentDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());

                        // > GET b
                        String Email = mData.get(position).getUserName().substring(0, 1);
                        // > b to B
                        String toUppercase = Email.toUpperCase();
                        // > B6x - NickName
                        String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
                        CommentDetailActivity.putExtra("textUser",ID_NAME);

                        CommentDetailActivity.putExtra("textEmail", mData.get(position).getUserName());

                        long timestamp = (long) mData.get(position).getTimeStamp();
                        CommentDetailActivity.putExtra("postDate", timestamp);

                        CommentDetailActivity.putExtra("textComments", mData.get(position).getContentComments());

                        CommentDetailActivity.putExtra("typeComments", mData.get(position).getCommentType());


                        //CommentDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());
                        mContext.startActivity(CommentDetailActivity);

                    }

                }
            });

            //Button Like
            likeBtn = itemView.findViewById(R.id.likeBtn);
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    testClick = true;
                    int position = getAdapterPosition();
                    commentKeyLike = mData.get(position).getCommentKey();
                    userID = mData.get(position).getUid();

                    likeReference = FirebaseDatabase.getInstance().getReference("likeComment");
                    likeReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (testClick == true) {

                                if (snapshot.child(commentKeyLike).hasChild(firebaseUser.getUid())) {

                                    //Remove Value Like
                                    likeReference.child(commentKeyLike).child(firebaseUser.getUid()).removeValue();
                                    testClick = false;

                                } else {

                                    //Set Value Like
                                    likeReference.child(commentKeyLike).child(firebaseUser.getUid()).setValue("true");
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

            //Button Check
            IconCheckTrue = itemView.findViewById(R.id.IconCheckTrue);
            trueBtn = itemView.findViewById(R.id.checkBtn);

            //Check My Post
            preferences = mContext.getSharedPreferences("PREFERENCES", MODE_PRIVATE);
            String checkMyPost = preferences.getString("SaveMyPost", "");

            if (checkMyPost.equals("Yes")) {

                trueBtn.setVisibility(View.VISIBLE);
                trueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        checkClick = true;
                        int position = getAdapterPosition();
                        commentCheckTrue = mData.get(position).getCommentKey();


                        trueReference = FirebaseDatabase.getInstance().getReference("commentCheckTrue");
                        trueReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (checkClick == true) {



                                    if (snapshot.child(commentCheckTrue).hasChild(firebaseUser.getUid())) {

                                        //Remove Value Like
                                        IconCheckTrue.setVisibility(View.INVISIBLE);
                                        textUser.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                                        trueReference.child(commentCheckTrue).child(firebaseUser.getUid()).removeValue();
                                        checkClick = false;

                                        //Save Key Comment
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("SaveKeyComment", "");
                                        editor.apply();

                                        //Refresh
                                        RefreshListview();

                                    } else {

                                        //Set Value Like
                                        IconCheckTrue.setVisibility(View.VISIBLE);
                                        textUser.setTextColor(ContextCompat.getColor(mContext, R.color.nav_color));
                                        trueReference.child(commentCheckTrue).child(firebaseUser.getUid()).setValue("true");
                                        checkClick = false;

                                        //Save Key Comment
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("SaveKeyComment", commentCheckTrue);
                                        editor.apply();

                                        //Refresh
                                        RefreshListview();

                                    }


                                }




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });


            } else {

                trueBtn.setVisibility(View.INVISIBLE);

            }




        }

        //Refresh Listview When Click Button Check True
        private void RefreshListview() {

            notifyDataSetChanged();

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
                    commentKey = mData.get(position).getPostKey();

                    //Remove Value Post
                    commentReference = FirebaseDatabase.getInstance().getReference("allPost");
                    commentReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child(commentKey).exists()) {

                                commentReference.child(commentKey).removeValue();

                            } else {
                                //Toast.makeText(mContext, "เกิดข้อผิดพลาด ลองอีกครั้ง", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //Remove Value Like
                    commentLikeReference = FirebaseDatabase.getInstance().getReference("likes");
                    commentLikeReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child(commentKey).exists()) {

                                commentLikeReference.child(commentKey).removeValue();

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

                            if (snapshot.child(commentKey).exists()) {

                                commentReference.child(commentKey).removeValue();

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
                    //bottomSheetDialogEditComment.show();

                }
            });

            builder.show();

        }

    }


    // >>> END Constructor <<< //

    // Timestamp To Time Ago
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
