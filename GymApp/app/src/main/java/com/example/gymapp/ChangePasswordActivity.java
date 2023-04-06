package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText edtCurrentPwd,edtNewPwd,edtNewPwdConfirm;
    private ProgressBar progressBarPwd;
    private TextView txtAuthenticatedPwd;
    private Button btnChangePwdAuth,btnUpdatePwd;
    private String userPwdCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");

        edtCurrentPwd = findViewById(R.id.edtCurrentPwd);
        edtNewPwd = findViewById(R.id.edtNewPwd);
        edtNewPwdConfirm = findViewById(R.id.edtNewPwdConfirm);
        progressBarPwd = findViewById(R.id.progressBarPwd);
        txtAuthenticatedPwd = findViewById(R.id.txtAuthenticatedPwd);
        btnChangePwdAuth = findViewById(R.id.btnChangePwdAuth);
        btnUpdatePwd = findViewById(R.id.btnUpdatePwd);

        edtNewPwd.setEnabled(false);
        edtNewPwdConfirm.setEnabled(false);
        btnUpdatePwd.setEnabled(false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(this, "something went wrong!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        btnChangePwdAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurrent = edtCurrentPwd.getText().toString();

                if (TextUtils.isEmpty(userPwdCurrent)){
                    Toast.makeText(ChangePasswordActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    edtCurrentPwd.setError("Please enter your current password");
                    edtCurrentPwd.requestFocus();
                }else {
                    progressBarPwd.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCurrent);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                progressBarPwd.setVisibility(View.GONE);

                                edtNewPwd.setEnabled(true);
                                edtNewPwdConfirm.setEnabled(true);
                                btnUpdatePwd.setEnabled(true);
                                edtCurrentPwd.setEnabled(false);
                                btnChangePwdAuth.setEnabled(false);

                                txtAuthenticatedPwd.setText("You are authenticated. " +"You can change password now!!");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified." + "Change password now!!", Toast.LENGTH_SHORT).show();
                                btnUpdatePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.dark_green));
                                
                                btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePassword(firebaseUser);
                                    }
                                });
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarPwd.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {
        String userNewPwd = edtNewPwd.getText().toString();
        String userNewPwdConfirm = edtNewPwdConfirm.getText().toString();

        if (TextUtils.isEmpty(userNewPwd)){
            Toast.makeText(this, "New password is Required", Toast.LENGTH_SHORT).show();
            edtNewPwd.setError("Please enter your new password!!");
            edtNewPwd.requestFocus();
        } else if (TextUtils.isEmpty(userNewPwdConfirm)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            edtNewPwd.setError("Please re-enter your new password!!");
            edtNewPwd.requestFocus();
        } else if (!userNewPwd.matches(userNewPwdConfirm)) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
            edtNewPwdConfirm.setError("Please re-enter same password");
            edtNewPwdConfirm.requestFocus();
        }else if (userNewPwd.matches(userPwdCurrent)) {
            Toast.makeText(this, "New password cannot be as same sa old password", Toast.LENGTH_SHORT).show();
            edtNewPwdConfirm.setError("Please enter a new password");
            edtNewPwdConfirm.requestFocus();
        }else {
            progressBarPwd.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userNewPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBarPwd.setVisibility(View.GONE);
                }
            });
        }
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
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.update_email){
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.settings_menu) {
            Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            auth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}