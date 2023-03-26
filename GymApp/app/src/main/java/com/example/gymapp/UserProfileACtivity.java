package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileACtivity extends AppCompatActivity {

    private TextView txtWelcome,txtName,txtEmail,txtDOB,txtGender,txtMobile;
    private ProgressBar progressBar3;
    private String name,email,dob,gender,mobile;
    private ImageView profileImage;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtWelcome = findViewById(R.id.txtWelcome);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtDOB = findViewById(R.id.txtDOB);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        progressBar3 = findViewById(R.id.progressBar3);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Something went wrong! User's details are not available at the moment.", Toast.LENGTH_SHORT).show();
        }else {

            progressBar3.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }

    }

    private void showUserProfile(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                if (readUserDetails != null){
                    name = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    dob = readUserDetails.dob;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    txtWelcome.setText("Welcome, " + name + "!");
                    txtName.setText(name);
                    txtEmail.setText(email);
                    txtDOB.setText(dob);
                    txtGender.setText(gender);
                    txtMobile.setText(mobile);

                }
                progressBar3.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UserProfileACtivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                progressBar3.setVisibility(View.GONE);

            }
        });

    }
}