package com.example.moreaboutmoreapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreaboutmoreapp.Activities.CommentDetailActivity;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;
    String commentKey, commentEditKey, commentKeyLike, userID;
    DatabaseReference commentLikeReference, commentReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

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

        Picasso.get().load(mData.get(position).getUserPhoto()).into(holder.userProfileComment);
        holder.textComment.setText(mData.get(position).getContentComments());
        holder.textTime.setText(timestampToString((Long)mData.get(position).getTimeStamp()));

        //Set Name
        // > GET b
        String Email = mData.get(position).getUserName().substring(0, 1);
        // > b to B
        String toUppercase = Email.toUpperCase();
        // > B6x - NickName
        String ID_NAME = toUppercase + mData.get(position).getUserName().substring(1, 3) + " - " + mData.get(position).getNickName();
        holder.textUser.setText(ID_NAME);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    // >>> END Implement Methods <<< //


    // Function Constructor 001
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        FirebaseDatabase db;
        ImageView userProfileComment;
        TextView textUser, textTime, textComment, textCountLike, textCountDisLike;

        BottomSheetDialog bottomSheetDialogEditComment;
        Boolean testClick = false;
        Button  MoreMenu, EditCommentButton;
        ToggleButton likeBtn, dislikeBtn;
        ProgressBar loadingProgress;

        TextInputLayout Edit_Comment;

        RelativeLayout layoutEdit, layoutDelete, layoutPin, layoutReport;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            db = FirebaseDatabase.getInstance();

            textUser = itemView.findViewById(R.id.textUser);
            textTime = itemView.findViewById(R.id.textTime);
            textComment = itemView.findViewById(R.id.textComment);
            userProfileComment = itemView.findViewById(R.id.userProfileComment);

            //Button MoreMenu
            MoreMenu = itemView.findViewById(R.id.MoreMenu);
            MoreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    commentEditKey = mData.get(position).getPostKey();
                    //Toast.makeText(mContext, commentEditKey+"", Toast.LENGTH_SHORT).show();

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



                        //CommentDetailActivity.putExtra("userPhotoImg",  mData.get(position).getUserPhoto());
                        mContext.startActivity(CommentDetailActivity);

                    }

                }
            });
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
