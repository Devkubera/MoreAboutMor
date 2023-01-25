package com.example.moreaboutmoreapp;

import static android.view.View.INVISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Activities.LoginActivity;
import com.example.moreaboutmoreapp.Activities.ManagePostActivity;
import com.example.moreaboutmoreapp.Adapters.ManagePostAdapter;
import com.example.moreaboutmoreapp.Models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagePostFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static BackKeyPressedListener backKeyPressedListener;

    public ManagePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagePostFragment newInstance(String param1, String param2) {
        ManagePostFragment fragment = new ManagePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onPause() {
        super.onPause();
        backKeyPressedListener = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        backKeyPressedListener = this;

    }

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int lastPosition;
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    List<Post> postList;
    RecyclerView managePostRecyclerView;
    ManagePostAdapter managePostAdapter;

    CircleImageView userProfile;
    ImageView Btn_BackManagePage;
    TextView txtNoneInfo;

    private ProgressBar loadingProgressData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_post, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("allPost");

        // TxtNoneInfo
        txtNoneInfo = view.findViewById(R.id.managePost_txtNoneInfo);

        // Progress Circle
        loadingProgressData = view.findViewById(R.id.managePost_ProgressData);

        // Back
        Btn_BackManagePage = view.findViewById(R.id.Btn_BackManagePost);
        Btn_BackManagePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Fragment newFragment = new SettingFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.managePostFragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
                getFragmentManager().popBackStack();
            }
        });

        //Retrieve Data List Post
        managePostRecyclerView = view.findViewById(R.id.managePostRV);
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
                    managePostAdapter = new ManagePostAdapter(getActivity(),postList);
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


        return view;
    }


}