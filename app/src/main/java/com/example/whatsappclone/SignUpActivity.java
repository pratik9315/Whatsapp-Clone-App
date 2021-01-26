package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaParser;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding; //new way to bind xml elements into java
    private FirebaseAuth mAuth; //firebase variable for authentication
    FirebaseDatabase database; //for storing database (realtime db)
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());//inflates the view?
        setContentView(binding.getRoot());//getRoot method being important in viewBinding.

        getSupportActionBar().hide();//hides the action bar
        mAuth = FirebaseAuth.getInstance();// gets the firebase and database instance
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account...");//Title
        progressDialog.setMessage("We are creating your Account...");//progress dialog message


        //when clicked on signup button, we instantiate the click with mAuth variable and assign two textviews email and password(and bind them using their id's)
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();//shows progress dialog when clicked on button
                mAuth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();// when the authentication task is complete
                       if(task.isSuccessful()){
                           //Users custom class file with 3 parameters
                           Users users = new Users(binding.name.getText().toString(), binding.email.getText().toString(), binding.pass.getText().toString());

                           //gets the task result, the user and the unique user id
                           String id = task.getResult().getUser().getUid();

                           //creates a child Users and sub child id with the given id and sets the users name as the value
                           database.getReference().child("Users").child(id).setValue(users);

                           Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_LONG).show();
                       }
                       else{
                           Toast.makeText(SignUpActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                       }
                    }
                });
            }
        });

        binding.already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(in);
            }
        });


    }
}