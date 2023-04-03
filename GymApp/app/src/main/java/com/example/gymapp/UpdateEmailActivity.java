package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBarUpdateEmail;
   private String userOldEmail, userNewEmail, userPassword;
    private TextView txtAuthenticated;
    private Button btnUpdateEmail;
    private TextInputEditText edtNewEmail, edtPassword,edtCurrentEmail;

    private static final String TAG = "UpdateEmailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        getSupportActionBar().setTitle("Update Email");

        progressBarUpdateEmail = findViewById(R.id.progressBarUpdateEmail);
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);
        edtNewEmail = findViewById(R.id.edtNewEmail);
        edtPassword = findViewById(R.id.edtUpdateEmailPassword);
        txtAuthenticated = findViewById(R.id.txtAuthenticated);
        edtCurrentEmail = findViewById(R.id.edtCurrentEmail);

        btnUpdateEmail.setEnabled(false);
        edtNewEmail.setEnabled(false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        //Set old email ID on TextView

        userOldEmail = firebaseUser.getEmail();
        edtCurrentEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }else {
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {

        Button btnAuthenticate = findViewById(R.id.btnUpdateEmailAuth);
        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPassword = edtPassword.getText().toString();

                if (TextUtils.isEmpty(userPassword)){
                    Toast.makeText(UpdateEmailActivity.this, "Password required", Toast.LENGTH_SHORT).show();
                    edtPassword.setError("Please enter your Password");
                    edtPassword.requestFocus();
                }else {
                    progressBarUpdateEmail.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                progressBarUpdateEmail.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified", Toast.LENGTH_SHORT).show();

                                txtAuthenticated.setText("You are authenticated. You can update your email now.");
                                edtNewEmail.setEnabled(true);
                                edtPassword.setEnabled(false);
                                btnAuthenticate.setEnabled(false);
                                btnUpdateEmail.setEnabled(true);

                                btnUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.dark_green));

                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = edtNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this, "New Email is required", Toast.LENGTH_SHORT).show();
                                            edtNewEmail.setError("Please enter new Email");
                                            edtNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                                            edtNewEmail.setError("Please provide valid Email");
                                            edtNewEmail.requestFocus();
                                        } else if (edtCurrentEmail.toString().matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New Email cannot be same as old Email", Toast.LENGTH_SHORT).show();
                                            edtNewEmail.setError("Please enter new Email");
                                            edtNewEmail.requestFocus();
                                        }else {
                                            progressBarUpdateEmail.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarUpdateEmail.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {

//       userNewEmail = edtNewEmail.getText().toString();
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated. Please verify your new Email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this,UserProfileACtivity.class);
                    startActivity(intent);
                    finish();

                    Log.d(TAG, "onComplete: new Email" + firebaseUser.getEmail() );

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(UpdateEmailActivity.this, "Email address already in use by another account.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBarUpdateEmail.setVisibility(View.GONE);
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
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.update_email){
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
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
            Intent intent = new Intent(UpdateEmailActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}