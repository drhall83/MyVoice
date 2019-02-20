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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();




        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);


        progressDialog = new ProgressDialog(LoginActivity.this);
        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //check email is empty
            Toast.makeText(this, "please enter your Email", Toast.LENGTH_SHORT).show();
            //stop the function from going further
            return;
        }
        if(TextUtils.isEmpty(password)){
            //check password is empty
            Toast.makeText(this, "please enter your Password", Toast.LENGTH_SHORT).show();
            //stop the function from going further
            return;
        }
        //if the email and passwords are not empty
        // show the progress dialog
        progressDialog.setMessage("Logging in......");
        progressDialog.show();


        //login the new user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                            //if successful
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

@Override
    public void onClick(View v) {

        if ( v == buttonLogin){
            userLogin();
        }
        if ( v == textViewSignUp){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        }




}
