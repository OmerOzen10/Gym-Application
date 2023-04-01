package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private TextInputEditText edtUpdateName, edtUpdateDoB, edtUpdateMobile;

    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String txtName, txtDoB, txtGender, txtMobile;
    private FirebaseAuth auth;
    private ProgressBar progressBarUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        edtUpdateName = findViewById(R.id.edtUpdateName);
        edtUpdateDoB = findViewById(R.id.edtUpdateDoB);
        edtUpdateMobile = findViewById(R.id.edtUpdateMobile);
        progressBarUpdate = findViewById(R.id.progressBarUpdate);

        radioGroupUpdateGender = findViewById(R.id.radioGroupUpdateGender);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();


        //Show Profile
        showProfile(firebaseUser);

        //Upload PProfile Pic
        Button btnUploadProfilePic = findViewById(R.id.btnUpdateProfilePic);
        btnUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this,UploadProfilePictureActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        //Update Email
//        Button btnUploadEmail = findViewById(R.id.btnUpdateEmail);
//        btnUploadEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        //DatePicker
        edtUpdateDoB.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String txtSADoB[] = txtDoB.split("/");

                int day = Integer.parseInt(txtSADoB[0]);
                int month = Integer.parseInt(txtSADoB[1]) - 1;
                int year = Integer.parseInt(txtSADoB[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOdMonth) {

                        edtUpdateDoB.setText(dayOdMonth + "/" + (month +1) + "/" + year);

                    }
                },year,month,day);
                picker.show();
            }
        });

        Button btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        if (TextUtils.isEmpty(txtName)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            edtUpdateName.setError("Full name is required");
            edtUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(txtDoB)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show();
            edtUpdateDoB.setError("Date of Birth is required");
            edtUpdateDoB.requestFocus();
        }else if (TextUtils.isEmpty(txtMobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your Phone Number", Toast.LENGTH_SHORT).show();
            edtUpdateMobile.setError("Number is required");
            edtUpdateMobile.requestFocus();
        } else if (txtMobile.length() !=9) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your Phone Number", Toast.LENGTH_SHORT).show();
            edtUpdateMobile.setError("Mobile number should be 9 digit");
            edtUpdateMobile.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        }else {
            txtGender = radioButtonUpdateGenderSelected.getText().toString();
            txtName = edtUpdateName.getText().toString();
            txtDoB = edtUpdateDoB.getText().toString();
            txtMobile = edtUpdateMobile.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtName,txtDoB,txtGender,txtMobile);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

            String userID = firebaseUser.getUid();

            progressBarUpdate.setVisibility(View.VISIBLE);

            reference.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(txtName).build();
                        firebaseUser.updateProfile(profileChangeRequest);

                        Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this,UserProfileACtivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBarUpdate.setVisibility(View.GONE);
                }
            });
        }

    }

    private void showProfile(FirebaseUser firebaseUser) {

        String userIdRegistered = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBarUpdate.setVisibility(View.VISIBLE);

        reference.child(userIdRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readWriteUserDetails != null){
                        txtName = readWriteUserDetails.name;
                        txtDoB = readWriteUserDetails.dob;
                        txtGender = readWriteUserDetails.gender;
                        txtMobile = readWriteUserDetails.mobile;

                        edtUpdateName.setText(txtName);
                        edtUpdateDoB.setText(txtDoB);
                        edtUpdateMobile.setText(txtMobile);

                        //For gender
                        if (txtGender.equals("Male")){
                            radioButtonUpdateGenderSelected = findViewById(R.id.radioMaleUpdate);
                        }else {
                            radioButtonUpdateGenderSelected = findViewById(R.id.radioFemaleUpdate);
                        }
                        radioButtonUpdateGenderSelected.setChecked(true);
                    }else {
                        Toast.makeText(UpdateProfileActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        
                    }
                    progressBarUpdate.setVisibility(View.GONE);
                    
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                progressBarUpdate.setVisibility(View.GONE);
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
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
//        }else if (id == R.id.update_email){
//            Intent intent = new Intent(UserProfileACtivity.this, UpdateEmailActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.settings_menu) {
//            Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.change_password) {
//            Intent intent = new Intent(UserProfileACtivity.this, ChangePasswordActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.delete_profile) {
//            Intent intent = new Intent(UserProfileACtivity.this, DeleteActivity.class);
//            startActivity(intent);
        } else if (id == R.id.logout) {
            auth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}