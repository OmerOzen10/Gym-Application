package com.example.gymapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    private TextView txtWelcome,txtName,txtEmail,txtDOB,txtGender,txtMobile;
    private ProgressBar progressBar3;
    private String name,email,dob,gender,mobile;
    private ImageView profileImage;
    private FirebaseAuth auth;
    String TAG = "UserProfileActivity";
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeToRefresh();

        txtWelcome = findViewById(R.id.txtWelcome);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtDOB = findViewById(R.id.txtDOB);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);
        profileImage = findViewById(R.id.imageProfile);



        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this,UploadProfilePictureActivity.class);
            startActivity(intent);
        });


        progressBar3 = findViewById(R.id.progressBar3);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Something went wrong! User's details are not available at the moment.", Toast.LENGTH_SHORT).show();
        }else {

            checkEmailVerified(firebaseUser);
            progressBar3.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }
        assert firebaseUser != null;
        Log.d(TAG, "onCreate: ok" + firebaseUser.isEmailVerified());

    }

    private void swipeToRefresh() {

        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(() -> {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
            swipeContainer.setRefreshing(false);
        });

      swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private void checkEmailVerified(FirebaseUser firebaseUser) {

        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();

        }

    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time");

        builder.setPositiveButton("Continue", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private void showUserProfile(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                if (readUserDetails != null){
                    name = readUserDetails.name;
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

                    //Set User DP after upload

                    Uri uri = firebaseUser.getPhotoUrl();

                    //ImageView setImageUri() should not be used with regular Uris. Use Picasso

//                    Picasso.get().load(uri).into(profileImage);\

                    Glide.with(UserProfileActivity.this).load(uri).into(profileImage);



                }else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
                progressBar3.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UserProfileActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                progressBar3.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh_menu){
            startActivity(getIntent());
            finish();
        } else if (id == R.id.update_profile) {
            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.update_email){
            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.settings_menu) {
            Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.change_password) {
            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.delete_profile) {
            Intent intent = new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            auth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}