package com.example.drhal.honsproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CHOOSE_IMAGE = 101;

    Uri uriProfileImage;
    FirebaseAuth firebaseAuth;

    private Button buttonLogout;
    ImageView profileImage;
    private DatabaseReference databaseReference;
    EditText editTextUserName;

    private Button createProfile;

    ProgressBar progressbar;
    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()== null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        editTextUserName =  findViewById(R.id.editTextUserName);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileImage = findViewById(R.id.profileImage);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        buttonLogout =  findViewById(R.id.buttonLogout);
        createProfile =  findViewById(R.id.createProfile);
        buttonLogout.setOnClickListener(this);
        createProfile.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        progressbar =  findViewById(R.id.progressbar);
        loadProfileInformation();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            loadProfileInformation();

        }
    }
    private void loadProfileInformation(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user.getPhotoUrl() !=null){
            Glide.with(this)
                    .load(user.getPhotoUrl().toString())
                    .into(profileImage);
        }
        if(user.getDisplayName()!= null){
            editTextUserName.setText(user.getDisplayName());
        }





    }

    private void saveUserInformation(){

        String displayName = editTextUserName.getText().toString().trim();

        if (displayName.isEmpty()){
            editTextUserName.setError("Enter profile name!");
            editTextUserName.requestFocus();
            return;

        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(profileImageUrl))
                .build();

        user.updateProfile(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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
                uploadImageToFirebaseStorage();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            progressbar.setVisibility(View.VISIBLE);


            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressbar.setVisibility(View.GONE);


                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();
                                    Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
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
            startActivity(new Intent(this, LoginActivity.class));
        }

        if ( v == profileImage){

            showImageSelection();

        }

        if ( v==  createProfile){
            Toast.makeText(ProfileActivity.this, "Profile created", Toast.LENGTH_SHORT).show();
            saveUserInformation();
          //  startActivity(new Intent(this, MyCategories.class));

        }
    }
}
