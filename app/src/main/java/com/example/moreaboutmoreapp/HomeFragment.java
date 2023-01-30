package com.example.moreaboutmoreapp;

import static android.view.View.INVISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moreaboutmoreapp.Activities.LoginActivity;
import com.example.moreaboutmoreapp.Activities.ProfileActivity;
import com.example.moreaboutmoreapp.Activities.SetupUserActivity;
import com.example.moreaboutmoreapp.Activities.SplashActivity;
import com.example.moreaboutmoreapp.Adapters.PostAdapter;
import com.example.moreaboutmoreapp.Models.NotificationClass;
import com.example.moreaboutmoreapp.Models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int lastPosition;
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    List<Post> postList;
    RecyclerView postRecyclerView;
    PostAdapter postAdapter;

    public static BackKeyPressedListener backKeyPressedListener;
    private ProgressBar loadingProgress;
    private ProgressBar loadingProgressData;
    private Button postButton;
    private TextInputLayout detailPost;
    FloatingActionButton addPost;

    TextView userName, textNoneInfo, textPosts, textPopular, textOld;
    CircleImageView userProfile;
    SearchView searchView;
    BottomSheetDialog bottomSheetDialog;

    private TextInputLayout STag;
    AutoCompleteTextView selectTag;
    ArrayAdapter<String> adapterItem;
    String[] tags = {"ปรึกษาการเรียน",
            "ลงทะเบียนเรียน",
            "หาเพื่อนทำงานกลุ่ม",
            "หาเพื่อนติวหนังสือ",
            "หาเพื่อนใหม่",
            "คุยเล่น",
            "อื่น ๆ"};

    String itemSelectTag;
    String filterPost = "";

    SharedPreferences sharedPreferences;
    private static final String SHARED_PROFILE_IMG = "myShared";
    private static final String KEY_IMG = "myURI";

    private boolean isPaused = false;

    // NotificationClass
    NotificationClass notificationClass = NotificationClass.getInstance();


    @Override
    public void onStart() {
        super.onStart();

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
    public void onPause() {
        super.onPause();
        backKeyPressedListener = null;

        //Save lastPosition RecyclerView onPause
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor dataPosition = getPrefs.edit();
        dataPosition.putInt("lastPosition", lastPosition);
        dataPosition.apply();

        //Save dataPause
        SharedPreferences getPrefs_Pause = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor dataPause = getPrefs_Pause.edit();
        dataPause.putString("dataPause", "Pause");
        dataPause.apply();

        isPaused = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        backKeyPressedListener = this;

        //Retrieve Last Position RecyclerView onResume
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lastPosition = getPrefs.getInt("lastPosition", 0);

        if (isPaused == true) {
            // The activity was paused, so do not update the RecyclerView
            //Set scrollToPosition
            postRecyclerView.scrollToPosition(lastPosition);
            //isPaused = false;
            //return;
        }

        // Update the RecyclerView
        String Prefs_Pause = getPrefs.getString("dataPause", "Pause");

        if (Prefs_Pause.equals("Pause")){
            postRecyclerView.scrollToPosition(lastPosition);

            if(lastPosition == 1) {
                postRecyclerView.scrollToPosition(0);
            } else {
                //postRecyclerView.scrollToPosition(lastPosition);
            }

        }


    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        showMessage("Test!");
        //getFragmentManager().popBackStack();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("allPost");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Check Set Name & Photo
        checkSetDataUser();

        loadingProgressData = view.findViewById(R.id.loadingProgressData);
        textNoneInfo = view.findViewById(R.id.textNoneInfo);
        //userName = view.findViewById(R.id.userName);
        //userName.setText(currentUser.getDisplayName());

        //Load User Profile
        userProfile = view.findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ProfileActivity = new Intent(getActivity(), ProfileActivity.class);
                startActivity(ProfileActivity);
            }
        });

        DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
        DatabaseReference imageRef = userDataRef.child("userPhoto");
        Log.d("SHOW USER PHOTO ", "onCreateView: " + imageRef);
        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String get_URI = snapshot.getValue().toString();
//                Picasso.get().load(get_URI).into(userProfile);
                if (get_URI != null || get_URI != "" || !get_URI.equals(null) || !get_URI.equals("")) {
                    Picasso.get().load(get_URI).into(userProfile);
                } else {
                    checkSetDataUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //SearchView custom font
        //searchView = view.findViewById(R.id.search);

        //int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        //TextView searchText = (TextView) searchView.findViewById(id);

        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.mitr_light);
        //searchText.setTypeface(typeface);
        //searchText.setTextColor(getResources().getColor(R.color.black));
        //searchText.setHintTextColor(getResources().getColor(R.color.dark_gray));

        //Filter Post
        textPosts = view.findViewById(R.id.textPosts);
        textPopular = view.findViewById(R.id.textPopular);
        textOld = view.findViewById(R.id.textOld);

        //Click New Post
        textPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textPosts.setTextColor(getResources().getColor(R.color.orange));
                textPosts.setTypeface(textPosts.getTypeface(), Typeface.BOLD);

                textPopular.setTextColor(getResources().getColor(R.color.grey_font_v2));
                textPopular.setTypeface(textPopular.getTypeface(), Typeface.NORMAL);

                textOld.setTextColor(getResources().getColor(R.color.grey_font_v2));
                textOld.setTypeface(textOld.getTypeface(), Typeface.NORMAL);

                filterPost = "NewPost";
                checkFilterPost(filterPost);

            }
        });

        //Click Popular Post
        textPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textPopular.setTextColor(getResources().getColor(R.color.orange));
                textPopular.setTypeface(textPopular.getTypeface(), Typeface.BOLD);

                textPosts.setTextColor(getResources().getColor(R.color.grey_font_v2));
                textPosts.setTypeface(textPosts.getTypeface(), Typeface.NORMAL);

                textOld.setTextColor(getResources().getColor(R.color.grey_font_v2));
                textOld.setTypeface(textOld.getTypeface(), Typeface.NORMAL);

                filterPost = "PopularPost";
                checkFilterPost(filterPost);

            }
        });

        //Click Old Post
        textOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textOld.setTextColor(getResources().getColor(R.color.orange));
                textOld.setTypeface(textOld.getTypeface(), Typeface.BOLD);

                textPosts.setTextColor(getResources().getColor(R.color.grey_font_v2));
                textPosts.setTypeface(textPosts.getTypeface(), Typeface.NORMAL);

                textPopular.setTextColor(getResources().getColor(R.color.grey_font_v2));
                textPopular.setTypeface(textPopular.getTypeface(), Typeface.NORMAL);

                filterPost = "OldPost";
                checkFilterPost(filterPost);


            }
        });

        //addPostBtn
        addPost = (FloatingActionButton) view.findViewById(R.id.addPostBtn);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);

                View BottomSheetView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.bottom_sheet_dialog, (RelativeLayout)view.findViewById(R.id.BottomSheetContainer));

                loadingProgress = BottomSheetView.findViewById(R.id.progressBarPost);
                postButton = BottomSheetView.findViewById(R.id.postBtn);
                detailPost = BottomSheetView.findViewById(R.id.detailPost);

                BottomSheetView.findViewById(R.id.postBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postButton.setVisibility(INVISIBLE);
                        loadingProgress.setVisibility(View.VISIBLE);

                        // Add All Data Go To Firebase
                        addPostBtn();


                    }
                });

                STag = BottomSheetView.findViewById(R.id.postTag);
                selectTag = BottomSheetView.findViewById(R.id.selectTag);
                adapterItem = new ArrayAdapter<String>(getActivity(),R.layout.list_item_tag,tags);
                selectTag.setAdapter(adapterItem);
                selectTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        itemSelectTag = adapterView.getItemAtPosition(i).toString();
                        //showMessage("Click : " + itemSelectTag);
                    }
                });


                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();



            }
        });


        //Retrieve Data List Post
        postRecyclerView = view.findViewById(R.id.postRV);
        //postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.scrollToPosition(lastPosition);

        //Get Position Scroll
        postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        addPost.setVisibility(INVISIBLE);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    postAdapter = new PostAdapter(getActivity(),postList);
                    postRecyclerView.setAdapter(postAdapter);


                    textNoneInfo.setVisibility(View.INVISIBLE);
                    loadingProgressData.setVisibility(INVISIBLE);
                    addPost.setVisibility(View.VISIBLE);

                } else {
                    textNoneInfo.setVisibility(View.VISIBLE);
                    loadingProgressData.setVisibility(INVISIBLE);
                    addPost.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Create Notification Channel
        // notificationClass.createNotificationChannel(getContext());

        return view;

    } // onCreateView


    //Function
    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void checkFilterPost(String filterPost) {

        if (filterPost.equals("OldPost")) {

            //Get List Posts Form The Database
            loadingProgressData.setVisibility(View.VISIBLE);
            addPost.setVisibility(INVISIBLE);

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

                        //Collections.reverse(postList);
                        postAdapter = new PostAdapter(getActivity(),postList);
                        postRecyclerView.setAdapter(postAdapter);

                        textNoneInfo.setVisibility(View.INVISIBLE);
                        loadingProgressData.setVisibility(INVISIBLE);
                        addPost.setVisibility(View.VISIBLE);

                    } else {
                        textNoneInfo.setVisibility(View.VISIBLE);
                        loadingProgressData.setVisibility(INVISIBLE);
                        addPost.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (filterPost.equals("PopularPost")) {
            //Get List Posts Form The Database
            loadingProgressData.setVisibility(View.VISIBLE);
            addPost.setVisibility(INVISIBLE);

            final ArrayList<String> statesArrayList = new ArrayList<>();

            DatabaseReference LikeRef = firebaseDatabase.getReference("likes");
            LikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap: snapshot.getChildren()) {
                        Log.e(snap.getKey(),snap.getChildrenCount() + "");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        postList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            postList.add(post);
                        }

                        DatabaseReference LikeRef = firebaseDatabase.getReference("likes");
                        LikeRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                String key = snapshot.getKey();
                                //showMessage(key);


                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //Sort Post by TimeStamp
                        Collections.sort(postList, new Comparator<Post>() {
                            @Override
                            public int compare(Post post, Post t1) {
                                return post.getTimeStamp().toString().compareToIgnoreCase(t1.getTimeStamp().toString());
                            }
                        });

                        Collections.reverse(postList);
                        postAdapter = new PostAdapter(getActivity(),postList);
                        postRecyclerView.setAdapter(postAdapter);

                        textNoneInfo.setVisibility(View.INVISIBLE);
                        loadingProgressData.setVisibility(INVISIBLE);
                        addPost.setVisibility(View.VISIBLE);

                    } else {
                        textNoneInfo.setVisibility(View.VISIBLE);
                        loadingProgressData.setVisibility(INVISIBLE);
                        addPost.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            //Get List Posts Form The Database
            loadingProgressData.setVisibility(View.VISIBLE);
            addPost.setVisibility(INVISIBLE);

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
                        postAdapter = new PostAdapter(getActivity(),postList);
                        postRecyclerView.setAdapter(postAdapter);

                        textNoneInfo.setVisibility(View.INVISIBLE);
                        loadingProgressData.setVisibility(INVISIBLE);
                        addPost.setVisibility(View.VISIBLE);

                    } else {
                        textNoneInfo.setVisibility(View.VISIBLE);
                        loadingProgressData.setVisibility(INVISIBLE);
                        addPost.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void checkSetDataUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference userDataRef = firebaseDatabase.getReference("userData/" + auth.getUid() + "/");
        DatabaseReference nameRef = userDataRef.child("name");
        DatabaseReference imageRef = userDataRef.child("userPhoto");
        Log.d("SHOW UID", String.valueOf(nameRef));
        Log.d("SHOW UID", auth.getUid());

        // Get Name
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SHOW UID value is ", snapshot.getValue().toString());
                String name = snapshot.getValue().toString();

                if(name.equals("default")) {
                    showMessage(name);
                    Intent setupUserActivity = new Intent(getActivity(), SetupUserActivity.class);
                    startActivity(setupUserActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SHOW GET NAME ERROR ", "onCancelled: " + error );
            }
        });

        // Get Photo
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SHOW Image value is ", snapshot.getValue().toString());
                String uri = snapshot.getValue().toString();

                if(uri.equals("default")) {
                    showMessage(uri);
                    Intent setupUserActivity = new Intent(getActivity(), SetupUserActivity.class);
                    startActivity(setupUserActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SHOW GET PHOTO ERROR ", "onCancelled: " + error );
            }
        });



        //Load Img Profile
        //sharedPreferences = getActivity().getSharedPreferences(SHARED_PROFILE_IMG, Context.MODE_PRIVATE);
        //String get_URI = sharedPreferences.getString(KEY_IMG, null);

        /*if (get_URI != null) {
            Picasso.get().load(get_URI).into(userProfile);
        } else {
            Picasso.get().load(R.drawable.img_profile).into(userProfile);
        }

         */


    }

    private boolean validatePost() {
        String detailInput = detailPost.getEditText().getText().toString().trim();

        if (detailInput.isEmpty()) {
            detailPost.setError("Field can't be empty");
            postButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            detailPost.setError(null);
            return true;
        }
    }

    private boolean validateSelectTag() {
        String tagInput = selectTag.getText().toString().trim();

        if (tagInput.isEmpty()) {
            STag.setError("Required");
            postButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            STag.setError(null);
            return true;
        }
    }

    private void addPostBtn() {

        if (!validatePost() | !validateSelectTag() ) {
            return;

        } else {
            //Get_Uri Profile
            DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
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

            /*DatabaseReference getImageRef = userDataRef.child("userPhoto");
            getImageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String image = snapshot.getValue().toString();
                    loadImageProfile(image);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/


        }

    }

    private void loadImageNameUser(String image, String name) {
        Post post = new Post(detailPost.getEditText().getText().toString(), currentUser.getUid(), currentUser.getDisplayName(), name, itemSelectTag, image);

        //Add Post to firebase
        addPost(post);
    }

    private void addPost(Post post) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("allPost").push();

        //Get post unique ID
        String key = myRef.getKey();
        post.setPostKey(key);

        //Add post data to firebase database
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //showMessage("Post added successfully");
                postButton.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(INVISIBLE);
                bottomSheetDialog.dismiss();

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
                            postAdapter = new PostAdapter(getActivity(),postList);
                            postRecyclerView.setAdapter(postAdapter);

                            textNoneInfo.setVisibility(View.INVISIBLE);
                            loadingProgressData.setVisibility(INVISIBLE);
                            addPost.setVisibility(View.VISIBLE);

                        } else {
                            textNoneInfo.setVisibility(View.VISIBLE);
                            loadingProgressData.setVisibility(INVISIBLE);
                            addPost.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

    }



}