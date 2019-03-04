package com.example.drhal.honsproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private ImageAdapter myImageAdapter;

    private ProgressBar myCircleProgressbar;

    private DatabaseReference myDatabaseReference;
    private List<Upload> myUploads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        myRecyclerView = findViewById(R.id.recycler_view);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myCircleProgressbar = findViewById(R.id.progress_circle);
        myUploads = new ArrayList<>();

        myDatabaseReference = FirebaseDatabase.getInstance().getReference("My_Images");
        myDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot  : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    myUploads.add(upload);
                }

                myImageAdapter = new ImageAdapter(ImagesActivity.this, myUploads);

                myRecyclerView.setAdapter(myImageAdapter);
                myCircleProgressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                myCircleProgressbar.setVisibility(View.INVISIBLE);

            }
        });
    }
}
