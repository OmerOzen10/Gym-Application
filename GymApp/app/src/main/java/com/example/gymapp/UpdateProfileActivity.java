package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        radioGroupUpdateGender = findViewById(R.id.radioGroupUpdateGender);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
    }
}