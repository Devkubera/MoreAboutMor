package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moreaboutmoreapp.Adapters.ManagePostAdapter;
import com.example.moreaboutmoreapp.Adapters.PostAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagePostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int lastPosition;
    final LinearLayoutManager layoutManager = new LinearLayoutManager(ManagePostActivity.this);
    List<Post> postList;
    RecyclerView managePostRecyclerView;
    ManagePostAdapter managePostAdapter;

    CircleImageView userProfile;
    ImageView Btn_BackManagePage;
    TextView txtNoneInfo;

    private ProgressBar loadingProgressData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("allPost");

        // TxtNoneInfo
        txtNoneInfo = findViewById(R.id.managePost_txtNoneInfo);

        // Progress Circle
        loadingProgressData = findViewById(R.id.managePost_ProgressData);

        // Back
        Btn_BackManagePage = findViewById(R.id.Btn_BackManagePost);
        Btn_BackManagePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Retrieve Data List Post
        managePostRecyclerView = findViewById(R.id.managePostRV);
        //postRecyclerView.setHasFixedSize(true);
        managePostRecyclerView.setLayoutManager(layoutManager);
        managePostRecyclerView.scrollToPosition(lastPosition);

        //Get Position Scroll
        managePostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.e("", String.valueOf(lastPosition));

            }
        });

        //Get List Posts Form The Database
        //showMessage("Get List");
        loadingProgressData.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    postList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        postList.add(post);
                    }


                    //Sort Post by TimeStamp
                    Collections.sort(postList, new Comparator<Post>() {
                        @Override
                        public int compare(Post post, Post t1) {
                            return post.getTimeStamp().toString().compareToIgnoreCase(t1.getTimeStamp().toString());
                        }
                    });

                    Collections.reverse(postList);
                    managePostAdapter = new ManagePostAdapter(getApplicationContext(),postList);
                    managePostRecyclerView.setAdapter(managePostAdapter);


                    txtNoneInfo.setVisibility(View.INVISIBLE);
                    loadingProgressData.setVisibility(INVISIBLE);
//                    addPost.setVisibility(View.VISIBLE);

                } else {
                    txtNoneInfo.setVisibility(View.VISIBLE);
                    loadingProgressData.setVisibility(INVISIBLE);
//                    addPost.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // End onCreate
    }
}