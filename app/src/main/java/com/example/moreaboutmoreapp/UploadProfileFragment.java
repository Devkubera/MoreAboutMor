package com.example.moreaboutmoreapp;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Activities.CropActivity;
import com.example.moreaboutmoreapp.Activities.LoginActivity;
import com.example.moreaboutmoreapp.Activities.MainActivity;
import com.example.moreaboutmoreapp.Activities.RulesActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.shuhart.stepview.StepView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadProfileFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadProfileFragment newInstance(String param1, String param2) {
        UploadProfileFragment fragment = new UploadProfileFragment();
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

    Uri pickedImgUri, image_selected_uri;
    ActivityResultLauncher<String> cropImage;
    private CircleImageView userProfile;
    static int RequestCode = 1;
    static int RequestCodeGallery = 1;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PROFILE_IMG = "myShared";
    private static final String KEY_IMG = "myURI";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String myUri = "";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;
    private StorageTask uploadTask;
    private StorageReference storageReferencePic;

    private ProgressBar loadingProgress;
    private Button setPhotoButton;

    StepView stepView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReferencePic = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_profile, container, false);

        loadingProgress = view.findViewById(R.id.progressBar);

        //Crop Image Profile
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(getActivity(), CropActivity.class);
            //intent.putExtra("SendImageData", result.toString());
            intent.putExtra("SendImageData",  result != null ? result.toString() : null);
            startActivityForResult(intent, 100);
        });

        userProfile = view.findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                }*/

                ImagePermission();


            }
        });

        //Upload Profile
        setPhotoButton = view.findViewById(R.id.setPhotoButton);
        setPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadProfileImage();
            }
        });

        //Step View
        stepView = view.findViewById(R.id.step_view);
        stepView.getState()
                .selectedTextColor(getResources().getColor(R.color.white))
                .animationType(StepView.ANIMATION_CIRCLE)
                .selectedCircleColor(getResources().getColor(R.color.white))
                .selectedCircleRadius(55)
                .selectedStepNumberColor(getResources().getColor( R.color.nav_color))
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add("ตั้งชื่อเล่น");
                    add("ตั้งรูปโปไฟล์");

                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(2)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .stepLineWidth(8)
                .textSize(50)
                .stepNumberTextSize(55)
                .typeface(ResourcesCompat.getFont(getActivity(), R.font.mitr_light))
                // other state methods are equal to the corresponding xml attributes
                .commit();

        stepView.go(1, true);

        return view;

    }


    // ****** checkAndRequestForPermission ******
    /*private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, RequestCode);
            }

        }
        else {
            openGallery();
        }

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, RequestCodeGallery);
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RequestCodeGallery && data != null) {

            //the user has successfully picked an image
            pickedImgUri = data.getData();
            userProfile.setImageURI(pickedImgUri);

        }
    }*/

    private void ImagePermission() {
        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 &&  resultCode == 101) {
            String result = data.getStringExtra("CROP");
            pickedImgUri = data.getData();

            if (result != null) {
                pickedImgUri = Uri.parse(result);
            }

            userProfile.setImageURI(pickedImgUri);

        } else {
            //Toast.makeText(getActivity(), "Not Select Pic", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadProfileImage() {

        setPhotoButton.setVisibility(INVISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);

        if (pickedImgUri != null) {

            final StorageReference fileRef = storageReferencePic
                    .child(currentUser.getUid() + ".jpg");

            uploadTask = fileRef.putFile(pickedImgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        /*HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image",myUri);*/

                        //Save on my phone
                        sharedPreferences = getActivity().getSharedPreferences(SHARED_PROFILE_IMG, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_IMG, myUri);
                        editor.apply();

                        //Save on firebase
                        DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
                        DatabaseReference photoRef = userDataRef.child("userPhoto");

                        photoRef.setValue(myUri).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                AlertBox();
                                loadingProgress.setVisibility(INVISIBLE);
                                stepView.done(true);

                            }
                        });


                    }

                }
            });




        } else {
            setPhotoButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);

            showMessage("กรุณาเลือกภาพโปรไฟล์ของคุณ");
            //Toast.makeText(getActivity(), "กรุณาเลือกภาพโปรไฟล์ของคุณ", Toast.LENGTH_SHORT).show();
        }

    }

    private void showMessage(String message) {

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.mitr_bold);
        SpannableString efr = new SpannableString(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            efr.setSpan(new TypefaceSpan(typeface), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Toast.makeText(getContext(), efr, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

    }

    //AlertBox
    private void AlertBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.setup_user_dialog, null);

        //Create AlertDialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        //Click To Login Activity
        Button click_ok = view.findViewById(R.id.okButton);
        click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sendButton.setVisibility(View.VISIBLE);
                //loadingProgress.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getActivity(), RulesActivity.class);
                startActivity(intent);


                //Close AlertDialog
                builder.dismiss();
            }
        });

        builder.show();

    }


    @Override
    public void onBackPressed() {
        //OpenFrag
        OpenFrag(new UploadProfileFragment());
    }

    public boolean OpenFrag(Fragment fragment) {
        if (fragment != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frag_setup, fragment)
                    .commit();
        }

        return true;
    }

}