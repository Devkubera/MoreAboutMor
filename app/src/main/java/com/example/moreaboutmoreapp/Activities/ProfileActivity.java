package com.example.moreaboutmoreapp.Activities;

import static android.view.View.INVISIBLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
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

import com.example.moreaboutmoreapp.Models.Comment;
import com.example.moreaboutmoreapp.Models.User;
import com.example.moreaboutmoreapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    ImageView Btn_BackProfilePage, editBtnName, editBtnGroup;
    ActivityResultLauncher<String> cropImage;
    private CircleImageView userProfile, editUserProfile;
    private ProgressBar progressBarProfile, progressBarName, progressBarMB;
    private Button saveImgButton, saveNameButton, saveMajorButton;
    BottomSheetDialog bottomSheetDialog;
    TextView touch;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String myUri = "";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;
    private StorageTask uploadTask;
    private StorageReference storageReferencePic;

    private TextInputLayout Email;
    private TextInputLayout Name;
    private TextInputLayout editNickName;
    private TextInputLayout SGroup, SBranch, EditSGroup, EditSBranch;

    Uri pickedImgUri;
    static int RequestCode = 1;
    static int RequestCodeGallery = 1;

    String[] groups = {"สำนักวิชาวิทยาศาสตร์",
            "สำนักวิชาเทคโนโลยีสังคม",
            "สำนักวิชาเทคโนโลยีการเกษตร",
            "สำนักวิชาวิศวกรรมศาสตร์",
            "สำนักวิชาแพทยศาสตร์",
            "สำนักวิชาพยาบาลศาสตร์",
            "สำนักวิชาทันตแพทยศาสตร์",
            "สำนักวิชาสาธารณสุขศาสตร์",
            "สำนักวิชาศาสตร์และศิลป์ดิจิทัล"};

    String[] branch_001 = {"เคมี", "คณิตศาสตร์", "ฟิสิกส์", "ชีววิทยา", "ภูมิสารสนเทศ", "วิทยาศาสตร์การกีฬา"};
    String[] branch_002 = {"เทคโนโลยีการจัดการ", "นวัตกรรมเทคโนโลยีอุตสาหกรรมบริการ"};
    String[] branch_003 = {"เทคโนโลยีการผลิตพืช",
            "เทคโนโลยีและนวัตกรรมทางสัตว์",
            "เทคโนโลยีอาหาร",
            "บูรณาการเทคโนโลยีการเกษตรและการจัดการความปลอดภัยด้านอาหาร"};
    String[] branch_004 = {"วิศวกรรมการผลิตอัตโนมัติและหุ่นยนต์",
            "วิศวกรรมการเกษตรและอาหาร",
            "วิศวกรรมขนส่งและโลจิสติกส์",
            "วิศวกรรมคอมพิวเตอร์",
            "วิศวกรรมเคมี",
            "วิศวกรรมเครื่องกล",
            "วิศวกรรมเซรามิก",
            "วิศวกรรมโทรคมนาคม",
            "วิศวกรรมธรณี",
            "วิศวกรรมปิโตรเลียมและเทคโนโลยีธรณี",
            "วิศวกรรมพอลิเมอร์",
            "วิศวกรรมอุตสาหการ",
            "วิศวกรรมยานยนต์",
            "วิศวกรรมโยธา",
            "วิศวกรรมโลหการ",
            "วิศวกรรมสิ่งแวดล้อม",
            "วิศวกรรมอากาศยาน",
            "วิศวกรรมอิเล็กทรอนิกส์",
            "วิศวกรรมไฟฟ้า",
            "วิศวกรรมเมคคาทรอนิกส์",
            "วิศวกรรมพรีซิชั่น",
            "วิศวกรรมโยธาและโครงสร้างพื้นฐาน",
            "วิศวกรรมไฟฟ้าอุตสาหกรรม"};
    String[] branch_005 = {"หลักสูตรแพทยศาสตรบัณฑิต"};
    String[] branch_006 = {"หลักสูตรพยาบาลศาสตรบัณฑิต"};
    String[] branch_007 = {"หลักสูตรทันตแพทยศาสตรบัณฑิต"};
    String[] branch_008 = {"อนามัยสิ่งแวดล้อม", "อาชีวอนามัยและความปลอดภัย"};
    String[] branch_009 = {"เทคโนโลยีดิจิทัล", "นิเทศศาสตร์ดิจิทัล"};

    String itemSelectGroup;
    String itemSelectBranch;

    AutoCompleteTextView selectGroup, selectBranch;
    ArrayAdapter<String> adapterItem, adapterBranch001, adapterBranch002, adapterBranch003, adapterBranch004, adapterBranch005, adapterBranch006, adapterBranch007, adapterBranch008, adapterBranch009;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PROFILE_IMG = "myShared";
    private static final String KEY_IMG = "myURI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Change Navigation Bar Color in Android
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.nav_color));


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReferencePic = FirebaseStorage.getInstance().getReference().child("Profile Pic");


        Btn_BackProfilePage = findViewById(R.id.Btn_BackProfile);
        Btn_BackProfilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Crop Image Profile
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(ProfileActivity.this, CropActivity.class);
            intent.putExtra("SendImageData",  result != null ? result.toString() : null);
            startActivityForResult(intent, 100);
        });


        //Get & Set User
        Email = findViewById(R.id.profileEmail);
        Email.getEditText().setText(currentUser.getEmail());

        //Get & Set Name
        Name = findViewById(R.id.editName);
        DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
        DatabaseReference getNameRef = userDataRef.child("name");

        getNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Get_Name = snapshot.getValue().toString();
                Name.getEditText().setText(Get_Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Get & Set Major
        SGroup = findViewById(R.id.editGroup);
        DatabaseReference getMajorRef = userDataRef.child("major");

        getMajorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Get_Major = snapshot.getValue().toString();
                SGroup.getEditText().setText(Get_Major);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Get & Set Sub-Major
        SBranch = findViewById(R.id.editBranch);
        DatabaseReference getSMajorRef = userDataRef.child("subMajor");

        getSMajorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Get_SMajor = snapshot.getValue().toString();
                SBranch.getEditText().setText(Get_SMajor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //EditProfile
        userProfile = findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialog);
                View BottomSheetView = LayoutInflater.from(ProfileActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_profile, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));


                editUserProfile = BottomSheetView.findViewById(R.id.editUserProfile);
                editUserProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePermission();
                    }
                });

                saveImgButton = BottomSheetView.findViewById(R.id.saveImgButton);
                saveImgButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UploadProfileImage();
                    }
                });

                progressBarProfile = BottomSheetView.findViewById(R.id.progressBarProfile);

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();

            }
        });

        //Get & Set Profile
        //sharedPreferences = getSharedPreferences(SHARED_PROFILE_IMG, MODE_PRIVATE);
        //String get_URI = sharedPreferences.getString(KEY_IMG, null);

        /*if (get_URI != null) {
            Picasso.get().load(get_URI).into(userProfile);
        } else {
            Picasso.get().load(R.drawable.img_profile).into(userProfile);
        }
         */

        DatabaseReference imageRef = userDataRef.child("userPhoto");
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String get_URI = snapshot.getValue().toString();
                Picasso.get().load(get_URI).into(userProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Edit Profile by Text
        touch = findViewById(R.id.touch);
        touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialog);
                View BottomSheetView = LayoutInflater.from(ProfileActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_profile, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerProfile));


                editUserProfile = BottomSheetView.findViewById(R.id.editUserProfile);
                editUserProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePermission();
                    }
                });

                saveImgButton = BottomSheetView.findViewById(R.id.saveImgButton);
                saveImgButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UploadProfileImage();
                    }
                });

                progressBarProfile = BottomSheetView.findViewById(R.id.progressBarProfile);

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();
            }
        });

        //Edit Name
        editBtnName = findViewById(R.id.editBtnName);
        editBtnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialog);
                View BottomSheetView = LayoutInflater.from(ProfileActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_name, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerName));

                //Get & Set Name
                editNickName = BottomSheetView.findViewById(R.id.editNickName);
                DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
                DatabaseReference getNameRef = userDataRef.child("name");

                getNameRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Get_Name = snapshot.getValue().toString();
                        editNickName.getEditText().setText(Get_Name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Save Name Button
                saveNameButton = BottomSheetView.findViewById(R.id.saveNameButton);
                saveNameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String updateName = editNickName.getEditText().getText().toString().trim();
                        updateUser(updateName);
                    }
                });
                progressBarName = BottomSheetView.findViewById(R.id.progressBarName);

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();
            }
        });

        //Edit Major
        editBtnGroup = findViewById(R.id.editBtnGroup);
        editBtnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialog);
                View BottomSheetView = LayoutInflater.from(ProfileActivity.this)
                        .inflate(R.layout.bottom_sheet_dialog_major, (RelativeLayout)view.findViewById(R.id.BottomSheetContainerMajor));



                //Select Group
                EditSGroup = BottomSheetView.findViewById(R.id.EditSGroup);
                selectGroup = BottomSheetView.findViewById(R.id.selectGroup);
                adapterItem = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,groups);
                selectGroup.setAdapter(adapterItem);

                //Select Branch
                EditSBranch = BottomSheetView.findViewById(R.id.EditSBranch);
                selectBranch = BottomSheetView.findViewById(R.id.selectBranch);
                adapterBranch001 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_001);
                adapterBranch002 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_002);
                adapterBranch003 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_003);
                adapterBranch004 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_004);
                adapterBranch005 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_005);
                adapterBranch006 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_006);
                adapterBranch007 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_007);
                adapterBranch008 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_008);
                adapterBranch009 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.list_item_group,branch_009);

                //Process Select Group & Branch
                selectGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        switch (position) {
                            case 0:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch001);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });

                                break;

                            case 1:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch002);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 2:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch003);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 3:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch004);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 4:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch005);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 5:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch006);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 6:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch007);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 7:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch008);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;

                            case 8:
                                itemSelectGroup = adapterView.getItemAtPosition(position).toString();

                                selectBranch.setText("");
                                selectBranch.setAdapter(adapterBranch009);
                                selectBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        itemSelectBranch = adapterView.getItemAtPosition(i).toString();
                                    }
                                });
                                break;



                        }
                    }
                });

                saveMajorButton = BottomSheetView.findViewById(R.id.saveMajorButton);
                saveMajorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateMajor_Branch(itemSelectGroup, itemSelectBranch);
                    }
                });

                progressBarMB = BottomSheetView.findViewById(R.id.progressBarMB);

                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }



    private void ImagePermission() {
        Dexter.withContext(ProfileActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(ProfileActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 &&  resultCode == 101) {
            String result = data.getStringExtra("CROP");
            pickedImgUri = data.getData();

            if (result != null) {
                pickedImgUri = Uri.parse(result);
            }

            editUserProfile.setImageURI(pickedImgUri);

        } else {
            //Toast.makeText(getActivity(), "Not Select Pic", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadProfileImage() {
        saveImgButton.setVisibility(INVISIBLE);
        progressBarProfile.setVisibility(View.VISIBLE);

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
                        sharedPreferences = getSharedPreferences(SHARED_PROFILE_IMG, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_IMG, myUri);
                        editor.apply();

                        //Save on firebase
                        DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
                        DatabaseReference photoRef = userDataRef.child("userPhoto");

                        showMessage("แก้ไขรูปโปรไฟล์เรียบร้อย");
                        saveImgButton.setVisibility(View.VISIBLE);
                        progressBarProfile.setVisibility(View.INVISIBLE);

                        photoRef.setValue(myUri).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //showMessage("แก้ไขข้อมูลเรียบร้อย");
                                bottomSheetDialog.dismiss();
                                userProfile.setImageURI(pickedImgUri);

                            }
                        });


                    }

                }
            });




        } else {
            saveImgButton.setVisibility(View.VISIBLE);
            progressBarProfile.setVisibility(View.INVISIBLE);

            showMessage("กรุณาเลือกภาพโปรไฟล์ของคุณ");
            //Toast.makeText(getActivity(), "กรุณาเลือกภาพโปรไฟล์ของคุณ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(String updateName) {
        saveNameButton.setVisibility(INVISIBLE);
        progressBarName.setVisibility(View.VISIBLE);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("userData").child(currentUser.getUid()).child("name");

        myRef.setValue(updateName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showMessage("แก้ไขชื่อเล่นเรียบร้อย");
                bottomSheetDialog.dismiss();
            }
        });

        // Update Name on my Post
        DatabaseReference namePostRef = db.getReference("allPost");
        namePostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userNameSnapshot: snapshot.getChildren()) {

                    String updateNickName = userNameSnapshot.child("userName").getValue(String.class);
                    String key = userNameSnapshot.getKey();
                    //showMessage(key);

                    if (updateNickName.equals(currentUser.getEmail())) {
                        namePostRef.child(key).child("nickName").setValue(updateName);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Update Name on my Comment
        DatabaseReference nameCommentRef = db.getReference("Comment");
        nameCommentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot PostKeySnapshot: snapshot.getChildren()) {
                    //loop 1 to go PostKeySnapshot
                    String PostKey = PostKeySnapshot.getKey();
                    //showMessage(PostKey);

                    for(DataSnapshot CommentKeySnapshot : snapshot.child(PostKey).getChildren()) {
                        //loop 2 to go CommentKey
                        String CommentKey = CommentKeySnapshot.getKey();
                        ///showMessage(CommentKey);

                        String updateNickNameComment = CommentKeySnapshot.child("userName").getValue(String.class);

                        // Update Name in Comment
                        if (updateNickNameComment.equals(currentUser.getEmail())) {
                            nameCommentRef.child(PostKey).child(CommentKey).child("nickName").setValue(updateName);
                        }

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private boolean validateSelectGroup() {
        String groupInput = selectGroup.getText().toString().trim();

        if (groupInput.isEmpty()) {
            EditSGroup.setError("Required");
            saveMajorButton.setVisibility(View.VISIBLE);
            progressBarMB.setVisibility(INVISIBLE);
            return false;

        } else {
            EditSGroup.setError(null);
            return true;
        }

    }

    private boolean validateSelectBranch() {
        String branchInput = selectBranch.getText().toString().trim();

        if (branchInput.isEmpty()) {
            EditSBranch.setError("Required");
            saveMajorButton.setVisibility(View.VISIBLE);
            progressBarMB.setVisibility(INVISIBLE);
            return false;

        } else {
            EditSBranch.setError(null);
            return true;
        }

    }

    private void updateMajor_Branch(String itemSelectGroup, String itemSelectBranch) {

        if (!validateSelectGroup() | !validateSelectBranch()) {
            return;
        } else {
            saveMajorButton.setVisibility(INVISIBLE);
            progressBarMB.setVisibility(View.VISIBLE);

            DatabaseReference ref = firebaseDatabase.getReference("userData").child(currentUser.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    deleteRankingSubject(user);
                }

                private void deleteRankingSubject(User user) {
                    DatabaseReference geRef = firebaseDatabase.getReference("allSubject")
                            .child("GeRelax")
                            .child(user.getMajor())
                            .child(user.getSubMajor())
                            .child(user.userId);
                    geRef.removeValue();
                    DatabaseReference majorRef = firebaseDatabase.getReference("allSubject")
                            .child("MajorSkillBranch")
                            .child(user.getMajor())
                            .child(user.getSubMajor())
                            .child(user.userId);
                    majorRef.removeValue();
                    Log.d("GeRelax", "deleteRankingSubject: " + geRef);
                    Log.d("MajorSkillBranch", "deleteRankingSubject: " + majorRef);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference updateUserDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
            updateUserDataRef.child("major").setValue(itemSelectGroup);
            updateUserDataRef.child("subMajor").setValue(itemSelectBranch);

            showMessage("แก้ไขสำนักวิชา และสาขาวิชาเรียบร้อย");

            bottomSheetDialog.dismiss();

        }

    }

    private void showMessage(String message) {

        Typeface typeface = ResourcesCompat.getFont(ProfileActivity.this, R.font.mitr_bold);
        SpannableString efr = new SpannableString(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            efr.setSpan(new TypefaceSpan(typeface), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Toast.makeText(ProfileActivity.this, efr, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
        }

    }


}