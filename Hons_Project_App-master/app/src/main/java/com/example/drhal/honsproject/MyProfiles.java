package com.example.drhal.honsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyProfiles extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserName;
    private Button buttonLogout;

    private DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profiles);



    }





    @Override
    public void onClick(View v) {



    }
}
