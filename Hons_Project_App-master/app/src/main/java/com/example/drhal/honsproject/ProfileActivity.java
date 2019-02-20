package com.example.drhal.honsproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CHOOSE_IMAGE = 101;

    Uri uriProfileImage;
    private FirebaseAuth firebaseAuth;

    private Button buttonLogout;
    ImageView profileImage;
    private DatabaseReference databaseReference;
     EditText editTextUserName;

     EditText editTextName;
    private Button createProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()== null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileImage = (ImageView) findViewById(R.id.profileImage);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        createProfile = (Button) findViewById(R.id.createProfile);
        buttonLogout.setOnClickListener(this);
        createProfile.setOnClickListener(this);
        profileImage.setOnClickListener(this);



//test link
    }

    private void saveUserInformation(){

        String name = editTextName.getText().toString().trim();
        UserInformation userInformation = new UserInformation(name );
        FirebaseUser user = firebaseAuth.getCurrentUser();
         databaseReference.child(user.getUid()).setValue(userInformation);
         Toast.makeText(this, "Saving your Information...", Toast.LENGTH_LONG).show();

    }

    private void showImageSelection(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
            profileImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {

        //if user presses logout button
        if(v == buttonLogout){
            Toast.makeText(ProfileActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

            //logging the user out
            firebaseAuth.signOut();
            //close activity
            finish();
            //start the login activity
            startActivity(new Intent(this, MainActivity.class));
        }

        if ( v == profileImage){
            Toast.makeText(ProfileActivity.this, "hello", Toast.LENGTH_SHORT).show();

            showImageSelection();
        }

        if ( v==  createProfile){
            Toast.makeText(ProfileActivity.this, "Profile created", Toast.LENGTH_SHORT).show();
        }
    }
}
