package com.example.gymapp;

import android.annotation.SuppressLint;
import android.companion.WifiDeviceFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout layoutName, layoutEmail, layoutDob, layoutMobile, layoutPassword, layoutConfirm;
    private TextInputEditText edtName, edtEmail, edtDob, edtMobile, edtPassword, edtConfirm;
    private ProgressBar progressBar;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGenderSelected;
    private static final String TAG = "RegisterActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        layoutName = findViewById(R.id.layoutName);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutDob = findViewById(R.id.layoutDob);
        layoutMobile = findViewById(R.id.layoutMobile);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirm = findViewById(R.id.layoutConfirm);


        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtDob = findViewById(R.id.edtDob);
        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);


        progressBar = findViewById(R.id.progressBar);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupGender.clearCheck();


        Button btnRegister = findViewById(R.id.button);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                radioButtonGenderSelected = findViewById(selectedGenderId);

                String txtName = edtName.getText().toString();
                String txtEmail = edtEmail.getText().toString();
                String txtDob = edtDob.getText().toString();
                String txtMobile = edtMobile.getText().toString();
                String txtPassword = edtPassword.getText().toString();
                String txtConfirm = edtConfirm.getText().toString();
                String txtGender;

                Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$");
                Matcher matcher = pattern.matcher(txtPassword);

                if (TextUtils.isEmpty(txtName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    edtName.setError("Full name is required");
                    edtName.requestFocus();
                } else if (TextUtils.isEmpty(txtEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    edtEmail.setError("Email is required");
                    edtEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter our Email", Toast.LENGTH_SHORT).show();
                    edtEmail.setError("Valid Email is required");
                    edtEmail.requestFocus();
                } else if (TextUtils.isEmpty(txtDob)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show();
                    edtDob.setError("Date of Birth is required");
                    edtDob.requestFocus();
                }else if (TextUtils.isEmpty(txtMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Phone Number", Toast.LENGTH_SHORT).show();
                    edtMobile.setError("Number is required");
                    edtMobile.requestFocus();
                } else if (txtMobile.length() !=9) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Phone Number", Toast.LENGTH_SHORT).show();
                    edtMobile.setError("Mobile number should be 9 digit");
                    edtMobile.requestFocus();
                } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    radioButtonGenderSelected.setError("Gender is required");
                    radioButtonGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    edtPassword.setError("Password is required");
                    edtPassword.requestFocus();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Password", Toast.LENGTH_SHORT).show();
                    edtPassword.setError("Password is too weak");
                    edtPassword.requestFocus();
                } else if (!matcher.matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Password", Toast.LENGTH_SHORT).show();
                    edtPassword.setError("Password is too weak");
                    edtPassword.requestFocus();

                } else if (TextUtils.isEmpty(txtConfirm)) {
                    Toast.makeText(RegisterActivity.this, "Please enter Password Confirmation", Toast.LENGTH_SHORT).show();
                    edtConfirm.setError("Password Confirmation is required");
                    edtConfirm.requestFocus();
                } else if (!txtPassword.equals(txtConfirm)) {
                    Toast.makeText(RegisterActivity.this, "Your Passwords do not matches!!", Toast.LENGTH_SHORT).show();
                    edtConfirm.setError("Confirm password should be same as the Password");
                    edtConfirm.requestFocus();
                    edtPassword.clearComposingText();
                    edtConfirm.clearComposingText();
                } else {
                    txtGender = radioButtonGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(txtName,txtEmail,txtDob,txtGender,txtMobile,txtPassword);
                }
            }
        });



    }



    private void registerUser(String txtName, String txtEmail, String txtDob, String txtGender, String txtMobile, String txtPassword) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification();
                    edtName.setText("");
                    edtEmail.setText("");
                    edtDob.setText("");
                    edtMobile.setText("");
                    edtConfirm.setText("");
                    edtPassword.setText("");
                    radioButtonGenderSelected.setText("");

//                    Intent intent = new Intent(RegisterActivity.this,UserProfileActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        edtPassword.setError("At least 1 Uppercase, 1 Lowercase and 1 Special Character");
                        edtPassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edtEmail.setError("Invalid Email");
                        edtEmail.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        edtEmail.setError("User is already registered with this email");
                        edtEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }
        });

    }

}
