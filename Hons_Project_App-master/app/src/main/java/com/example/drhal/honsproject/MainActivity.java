package com.example.drhal.honsproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);



        //initialize Firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the current user does not return null
        if (firebaseAuth.getCurrentUser() != null) {

            //the user must be logged in already so close this activity
            //profile activity here
            finish();
            // open the profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        //initialize views
        buttonRegister =  findViewById(R.id.buttonRegister);
        editTextEmail =  findViewById(R.id.editTextEmail);
        editTextPassword =  findViewById(R.id.editTextPassword);
        textViewSignIn =  findViewById(R.id.textViewSignIn);
        progressDialog = new ProgressDialog(MainActivity.this);

        //listeners for the buttons
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    private void registerUser() {

        //get the email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //check if the email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter your Email", Toast.LENGTH_SHORT).show();
            //stop the function from going further
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //email is empty
            Toast.makeText(this, "please enter your Password", Toast.LENGTH_SHORT).show();
            //stop the function from going further
            return;
        }


        //if the email and password are not empty
        // show the progress dialog
        progressDialog.setMessage("Registering User......");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking is successful
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                        } else {
                            Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();///
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));

        }
    }

    @Override
    public void onClick(View v) {
    if (v == buttonRegister) {
        registerUser();
    }
    if (v == textViewSignIn){


        startActivity(new Intent(this, LoginActivity.class));

    }
}


}
