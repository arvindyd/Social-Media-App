package com.radhe.facebookclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.radhe.facebookclone.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        auth= FirebaseAuth.getInstance();
        currentUser= auth.getCurrentUser();
        ProgressDialog progressDialog= new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Creating Your Account");
        progressDialog.setMessage("your account is creating");

        binding.logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }

                            }
                        });
            }
        });


        binding.creatAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();

            }
        });

        if (currentUser!=null){

            Intent intent= new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }


    }



}