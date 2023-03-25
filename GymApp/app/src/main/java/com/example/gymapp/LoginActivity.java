package com.example.gymapp;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail1, edtPassword1;
    private ProgressBar progressBar1;
    private FirebaseAuth auth;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail1 = findViewById(R.id.edtEmail1);
        edtPassword1 = findViewById(R.id.edtPassword1);
        progressBar1 = findViewById(R.id.progressBar1);

        auth = FirebaseAuth.getInstance();

//        ImageView hidePass = findViewById(R.id.hidePass);
//
//        hidePass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        Button btnLogin = findViewById(R.id.btnLogin1);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textEmail = edtEmail1.getText().toString();
                String txtPassword = edtPassword1.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    edtEmail1.setError("Email is required");
                    edtEmail1.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    edtEmail1.setError("Invalid Email");
                    edtEmail1.requestFocus();
                } else if (TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    edtPassword1.setError("Password is required");
                    edtPassword1.requestFocus();
                }else {
                    progressBar1.setVisibility(View.VISIBLE);
                    loginUser(textEmail,txtPassword);
                }

            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();

                }else {

                    try {
                        throw task.getException();

                    }catch (FirebaseAuthInvalidUserException e){
                        edtEmail1.setError("User does not exist");
                        edtEmail1.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edtEmail1.setError("Invalid credentials");
                        edtEmail1.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
                progressBar1.setVisibility(View.GONE);
            }
        });
    }
}
