package com.example.moreaboutmoreapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moreaboutmoreapp.Activities.CommentDetailActivity;
import com.example.moreaboutmoreapp.Activities.PostDetailActivity;
import com.example.moreaboutmoreapp.Activities.SelectStickerActivity;
import com.example.moreaboutmoreapp.Models.Comment;
import com.example.moreaboutmoreapp.Models.Sticker;
import com.example.moreaboutmoreapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<Sticker> mData;
    SharedPreferences preferences;
    //List<Comment> mData;


    public StickerAdapter(Context mContext, ArrayList<Sticker> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public StickerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_sticker,parent,false);
        return new MyViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull StickerAdapter.MyViewHolder holder, int position) {

        //Set Img Profile
        //Picasso.get().load(mData).into(holder.imgSticker);
        Glide.with(mContext).load(mData.get(position).getStickerUrl()).into(holder.imgSticker);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int positionUrl = holder.getAdapterPosition();
                String url = mData.get(positionUrl).getStickerUrl();

                //Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();

                //Open Select Sticker Activity
                Intent SelectStickerActivity = new Intent(mContext, SelectStickerActivity.class);
                SelectStickerActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // get receiver uid
                //Check First User

                preferences = mContext.getSharedPreferences("uidReceiver", MODE_PRIVATE);
                String uid = preferences.getString("uid", "");
                String nickname = preferences.getString("nickname", "");


                //Put Data
                SelectStickerActivity.putExtra("StickerUrl", mData.get(positionUrl).getStickerUrl());
                SelectStickerActivity.putExtra("uidReceiver", uid);
                SelectStickerActivity.putExtra("nickname", nickname);


                mContext.startActivity(SelectStickerActivity);



            }
        });


    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //ImageView imgSticker;
        BottomSheetDialog bottomSheetDialogSelectSticker;
        GifImageView imgSticker;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSticker = itemView.findViewById(R.id.imgSticker);


        }

    }



}
