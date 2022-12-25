package com.example.moreaboutmoreapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.moreaboutmoreapp.R;
import com.example.moreaboutmoreapp.UploadProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropActivity extends AppCompatActivity {

    String sourceUri, destinationUri;
    Uri uri;

    /*@Override
    public void onBackPressed() {
        //OpenFrag
        OpenFrag(new UploadProfileFragment());
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            sourceUri = intent.getStringExtra("SendImageData");

            if (sourceUri != null) {
                uri = Uri.parse(sourceUri);

                destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

                UCrop.Options options = new UCrop.Options();
                //options.setToolbarColor(ContextCompat.getColor(this, R.color.nav_color));
                options.setStatusBarColor(ContextCompat.getColor(this, R.color.nav_color));

                UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withOptions(options)
                        //.withAspectRatio(16, 9)
                        .withMaxResultSize(2000, 2000)
                        .start(CropActivity.this);

            } else {
                finish();
            }


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            Intent intent = new Intent();
            intent.putExtra("CROP", resultUri+"");
            setResult(101, intent);
            finish();

        } /*else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }*/

        else {
            finish();
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean OpenFrag(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_setup, fragment)
                    .commit();
        }
        return true;
    }

}