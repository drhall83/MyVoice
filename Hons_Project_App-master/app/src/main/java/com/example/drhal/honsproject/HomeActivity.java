package com.example.drhal.honsproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth firebaseAuth;

    private Button myButtonChooseImage;
    private Button myButtonUpload;
    private TextView myTextViewShowUploads;
    private EditText myEditTextFileName;
    private ImageView myImageView;
    private ProgressBar myProgressBar;
    private Button buttonLogout;
    private Uri myImageUri;

    private StorageReference myStorageReference;
    private DatabaseReference myDatabaseReference;

    private StorageTask myUploadTask;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()== null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }



        myButtonChooseImage = findViewById(R.id.button_choose_image);
        myButtonUpload = findViewById(R.id.button_upload);
        myTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        myEditTextFileName = findViewById(R.id.edit_text_file_name);
        myImageView = findViewById(R.id.view_image);
        myProgressBar = findViewById(R.id.progressbar);
        buttonLogout = findViewById(R.id.buttonLogout);
        myDatabaseReference = FirebaseDatabase.getInstance().getReference("My_Images");
        myStorageReference = FirebaseStorage.getInstance().getReference("My_Images");
        myButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openFileChooser();
            }

        });

        myButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myUploadTask != null && myUploadTask.isInProgress()){
                    Toast.makeText(HomeActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();

                }
            }
        });

        myTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null){
            myImageUri = data.getData();

            Picasso.get().load(myImageUri).into(myImageView);
        }


    }

    private  String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }




    private void uploadFile() {
        if (myImageUri != null) {
            StorageReference fileReference = myStorageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(myImageUri));

             myUploadTask = fileReference.putFile(myImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        private static final String TAG ="My_Images" ;
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                //    myProgressBar.setProgress(0);
                                }
                            }, 500);

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());
                            Upload upload = new Upload(myEditTextFileName.getText().toString().trim(),downloadUrl.toString());


                            String uploadId = myDatabaseReference.push().getKey();
                            myDatabaseReference.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                          //  double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                           // myProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity(){
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
