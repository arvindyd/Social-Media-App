package com.radhe.facebookclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {


    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        ProgressDialog progressDialog= new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Your Account");
        progressDialog.setMessage("your account is creating");



        auth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        binding.alreadyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();
                                if (task.isSuccessful()){

                                    Users user= new Users(binding.userName.getText().toString(),binding.etEmail.getText().toString(),binding.etPassword.getText().toString());
                                    String id= task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);

                                    Intent intent= new Intent(SignUpActivity.this,MainActivity.class);
                                    startActivity(intent);

                                }else {

                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });



    }
}