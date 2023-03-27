package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btnResetPassword;
    private TextInputLayout layoutResetEmail;
    private TextInputEditText edtResetPassword;
    private ProgressBar progressBarReset;
    private FirebaseAuth auth;
    private final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnResetPassword = findViewById(R.id.btnResetPassword);
        layoutResetEmail = findViewById(R.id.layoutResetEmail);
        edtResetPassword = findViewById(R.id.edtResetEmail);
        progressBarReset = findViewById(R.id.progressBarReset);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtResetPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    edtResetPassword.setError("Email is required");
                    edtResetPassword.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter valid", Toast.LENGTH_SHORT).show();
                    edtResetPassword.setError("Valid Email is required");
                    edtResetPassword.requestFocus();
                } else {
                   progressBarReset.setVisibility(View.VISIBLE);
                   resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {

        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your mail for password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        edtResetPassword.setError("User does not exist");
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBarReset.setVisibility(View.GONE);
            }
        });

    }
}