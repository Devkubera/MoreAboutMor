package com.example.moreaboutmoreapp;

import static android.view.View.INVISIBLE;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Activities.LoginActivity;
import com.example.moreaboutmoreapp.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
    public void onPause() {
        super.onPause();
        backKeyPressedListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        backKeyPressedListener = this;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        //Clear Activity Stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    Uri pickedImgUri;
    ImageView userProfile, Btn_BackRegisPage;
    TextView textEmail, textPass, textForget, textHaveAcc, C_Login;
    private EditText loginName, loginEmail, loginPass, loginConfirmPass;
    private ProgressBar loadingProgress;
    private Button regButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    public static BackKeyPressedListener backKeyPressedListener;

    private TextInputLayout Name;
    private TextInputLayout Email;
    private TextInputLayout Pass;
    private TextInputLayout PassCon;
    private TextInputLayout SGroup, SBranch;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Input Users
        //Name = view.findViewById(R.id.regName);
        Email = view.findViewById(R.id.regEmail);
        Pass = view.findViewById(R.id.regPassword);
        PassCon = view.findViewById(R.id.regConfirmPassword);
        regButton = view.findViewById(R.id.regButton);
        loadingProgress = view.findViewById(R.id.progressBar);

        //Button Back
        Fragment me = this;
        Btn_BackRegisPage = view.findViewById(R.id.Btn_BackRegisPage);
        Btn_BackRegisPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), LoginActivity.class);

                //Clear Activity Stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                getActivity().finish();

            }
        });

        //Select Group
        SGroup = view.findViewById(R.id.regGroup);
        selectGroup = view.findViewById(R.id.selectGroup);
        adapterItem = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,groups);
        selectGroup.setAdapter(adapterItem);

       /* selectGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemSelectGroup = adapterView.getItemAtPosition(i).toString();
                showMessage(itemSelectGroup);
            }
        });*/


        //Select Branch
        SBranch = view.findViewById(R.id.regBranch);
        selectBranch = view.findViewById(R.id.selectBranch);
        adapterBranch001 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_001);
        adapterBranch002 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_002);
        adapterBranch003 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_003);
        adapterBranch004 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_004);
        adapterBranch005 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_005);
        adapterBranch006 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_006);
        adapterBranch007 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_007);
        adapterBranch008 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_008);
        adapterBranch009 = new ArrayAdapter<String>(getActivity(),R.layout.list_item_group,branch_009);

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


        //Register
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regButton.setVisibility(INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                //final String name = Name.getEditText().getText().toString().trim();
                final String email = Email.getEditText().getText().toString().trim();
                final String pass = Pass.getEditText().getText().toString().trim();
                final String passConfirm = PassCon.getEditText().getText().toString().trim();

                /*if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showMessage("Email Verified !");
                } else {
                    showMessage("Enter valid Email address !");
                }*/

                /*if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || !pass.equals(passConfirm)) {
                    showMessage("Please Verify All Fields");
                    regButton.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else {
                    //Create User Account
                    CreateAcc(email, pass);
                }*/


                registerBtn();

            }
        });

        return view;

    }


    //Function
    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        String nameInput = Name.getEditText().getText().toString().trim();

        if (nameInput.isEmpty()) {
            Name.setError("Field can't be empty");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            Name.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = Email.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            Email.setError("กรุณากรอกอีเมล");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Email.setError("รูปแบบของอีเมลไม่ถูกต้อง");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            String[] split = emailInput.split("@");
            String domain = split[1];

            if(domain.equals("g.sut.ac.th")) {
                Email.setError(null);
                return true;
            } else {
                Email.setError("กรุณาใช้อีเมล มทส. ในการสมัครใช้งาน");
            }

            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else {
            Email.setError(null);
            //Email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePass() {
        String passInput = Pass.getEditText().getText().toString().trim();

        if (passInput.isEmpty()) {
            Pass.setError("กรุณากรอกรหัสผ่าน");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;
        } else {
            Pass.setError(null);
            return true;
        }
    }

    private boolean validatePassCon() {
        String passInput = Pass.getEditText().getText().toString().trim();
        String passConInput = PassCon.getEditText().getText().toString().trim();

        if (!passInput.equals(passConInput)) {
            PassCon.setError("รหัสผ่านไม่ตรงกัน");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else if (passInput.isEmpty()) {
            PassCon.setError("กรุณากรอกรหัสผ่านอีกครั้ง");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else {
            PassCon.setError(null);
            return true;
        }

    }

    private boolean validateSelectGroup() {
        String groupInput = selectGroup.getText().toString().trim();

        if (groupInput.isEmpty()) {
            SGroup.setError("กรุณาเลือกสำนักวิชาของคุณ");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else {
            SGroup.setError(null);
            return true;
        }

    }

    private boolean validateSelectBranch() {
        String branchInput = selectBranch.getText().toString().trim();

        if (branchInput.isEmpty()) {
            SBranch.setError("กรุณาเลือกสาขาวิชาของคุณ");
            regButton.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(INVISIBLE);
            return false;

        } else {
            SBranch.setError(null);
            return true;
        }

    }

    public void registerBtn() {
        String emailInput = Email.getEditText().getText().toString().trim();
        String passInput = Pass.getEditText().getText().toString().trim();

        if (!validateEmail() | !validatePass() | !validatePassCon() | !validateSelectGroup() | !validateSelectBranch()) {
            return;

        } else {
            //Create User Account
            CreateAcc(emailInput, passInput);
        }

    }

    private void CreateAcc(String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //showMessage("Account created");
                            updateUserInfo(email, mAuth.getCurrentUser());

                        }
                        else {
                            //showMessage("Account creation failed");
                            Toast.makeText(getContext(), "Error : "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            regButton.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(INVISIBLE);

                        }
                    }
                });

    }


    /*private void CreateAcc(String name, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    showMessage("Account created");
                    updateUserInfo(name, mAuth.getCurrentUser());
                }
                else {
                    showMessage("Account creation failed" + task.getException().getMessage());
                    regButton.setVisibility(View.INVISIBLE);

                }
            });

        }
    }*/

    private void updateUserInfo(String email, FirebaseUser currentUser) {
        //Upload Photo
        //StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photo");
        //StorageReference imageFilePath = mStorage.child("-");
        //imageFilePath.putFile();

        /*DatabaseReference userDataRef = firebaseDatabase.getReference("userData").child(currentUser.getUid());
        DatabaseReference imageRef = userDataRef.child("userPhoto");
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        UserProfileChangeRequest  profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(email)
                .build();

        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //showMessage("Register Complete");
                            //UpdateUI();

                            //Send EmailVerified
                            FirebaseUser userVerified = FirebaseAuth.getInstance().getCurrentUser();
                            userVerified.sendEmailVerification();

                            //Save UserData
                            User user = new User(currentUser.getUid(), email, itemSelectGroup, itemSelectBranch, "default", "default");
                            addDataUser(user, currentUser.getUid());

                            AlertBox();

                        }


                    }
                });

    }

    private void addDataUser(User user, String uid) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("userData").child(uid);

        //Add post data to firebase database
        myRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showMessage("successfully");

            }
        });

    }

    private void updateUserInfoWithoutPhoto(String name, FirebaseUser currentUser) {

        UserProfileChangeRequest  profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //showMessage("Register Complete");
                            //UpdateUI();

                            //Send EmailVerified
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification();

                            AlertBox();
                        }

                    }
                });

    }

    private void UpdateUI() {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag, new HomeFragment()).commit();
    }

    //AlertBox
    private void AlertBox() {

       LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.email_verification_dialog, null);

        //Create AlertDialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        //Click To Login Activity
        Button click_ok = view.findViewById(R.id.click_ok);
        click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                //Clear Activity Stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                //Close AlertDialog
                builder.dismiss();
            }
        });

        builder.show();

    }



}