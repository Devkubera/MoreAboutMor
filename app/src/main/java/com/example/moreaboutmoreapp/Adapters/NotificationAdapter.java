package com.example.moreaboutmoreapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moreaboutmoreapp.Models.NotificationData;
import com.example.moreaboutmoreapp.Models.Post;
import com.example.moreaboutmoreapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Context mContext;
    List<NotificationData> mData;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference notificationReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    String type[];
    String email;

    // get uid
    public static String user_Id;

    public NotificationAdapter(Context mContext, List<NotificationData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 12.37
        View notyList = LayoutInflater.from(mContext).inflate(R.layout.row_notification,parent,false);
        return new MyViewHolder(notyList);

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {

        String Title = mData.get(position).getTitle();
        String timer = mData.get(position).getTimer();
        // String sumText = Title + "  " + timer;

        // cut off name
        type = Title.split(" ");

        String TypeNotification = mData.get(position).getTypes();
        if (TypeNotification.equals("like post") || TypeNotification.equals("like comment")) {
            holder.imgIcon.setImageResource(R.drawable.ic_round_thumb_up_alt_24);
        } else if (TypeNotification.equals("pin comment")) {
            holder.imgIcon.setImageResource(R.drawable.ic_round_push_pin_24);
        } else if (TypeNotification.equals("post moment")) {
            holder.imgIcon.setImageResource(R.drawable.ic_round_mode_comment_24);
        }

        String uid = mData.get(position).getUidPusher();

        // get email
        notificationReference = FirebaseDatabase.getInstance().getReference("userData").child(uid).child("email");
        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email = snapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // update name user follow by newest data
        notificationReference = FirebaseDatabase.getInstance().getReference("userData").child(uid).child("name");
        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nickname = snapshot.getValue().toString();

                    /** make nickname with BXX */
                    // > GET b
                    String Email = email.substring(0, 1);
                    // > b to B
                    String toUppercase = Email.toUpperCase();
                    // > B6x - NickName
                    String ID_NAME = toUppercase + email.substring(1, 3) + " - " + nickname;

                    String sumText = ID_NAME + "  " + type[1] + " " +timer;
                    holder.tvContent.setText(sumText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // get uid pusher to load picture profile
        notificationReference = FirebaseDatabase.getInstance().getReference("userData").child(uid).child("userPhoto");
        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String photo = snapshot.getValue().toString();
                    Uri photoURI = Uri.parse(photo);
                    Picasso.get().load(photoURI).into(holder.imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // load img picture
//        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgProfile);
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        user_Id = firebaseAuth.getUid();
//
//        // get data notification from database.
//        notificationReference = firebaseDatabase.getReference("NotificationCenter").child(user_Id);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        ImageView imgProfile, imgIcon;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.row_noty_txtHeader);
            imgProfile = itemView.findViewById(R.id.row_noty_profile);
            imgIcon = itemView.findViewById(R.id.row_noty_icon);
        }
    }
}
