package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.databinding.ActivitySignInBinding;
import com.example.whatsappclone.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding; //new way to bind xml elements into java
    private FirebaseAuth mAuth; //firebase variable for authentication
    ProgressDialog progressDialog; //progress bar
    GoogleSignInClient mgoogleSignInClient; // google sign in purposes
    FirebaseDatabase database; // realtime database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());//inflates the view?
        setContentView(binding.getRoot());//getRoot method being important in viewBinding.

        getSupportActionBar().hide();//hides the action bar
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();// gets the firebase and database instance

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login"); //progress bar title
        progressDialog.setMessage("We are Logging in to your Account..."); //progress bar main message

        //is used to build and show google sign in options..is copied from official firebase
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)// builds the option menus
                .requestIdToken(getString(R.string.default_web_client_id)) //requests token id
                .requestEmail() //requests user email
                .build(); //builds
        mgoogleSignInClient = GoogleSignIn.getClient(this, gso); //intializes google sign in client with sign in options


        //when clicked on sign up button..
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.etEmail.getText().toString().isEmpty()){
                    binding.etEmail.setError("Please enter email");
                    return;
                }

                if(binding.passWord.getText().toString().isEmpty()){
                    binding.passWord.setError("Please enter password");
                    return;
                }
                progressDialog.show();//shows progress dialog as soon as the button is clicked
                //asks email and password as a text and converts them into string
                mAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.passWord.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //onComplete listener whenever a task is complete, in our case it is called after signup is clicked
                    //and when we get the email and password
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();// progress dialog is dismissed as user has entered email and password and the firebase is creating the accounnt in the backend..
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);// if the task is successful, the user is signed in and redirected to main activity
                            onBackPressed();
                        }

                        else{
                            Toast.makeText(SignInActivity.this, "FAIL",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        binding.already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(in); //user is redirected to sign up activity
            }
        });

        //Google sign in
        binding.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //if user is not null
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }//keeps the user logged in
    }


    int RC_SIGN_IN = 65; //RC code?
    private void signIn() {
        Intent signInIntent = mgoogleSignInClient.getSignInIntent();// a kind of intent for opening list of mails
        startActivityForResult(signInIntent, RC_SIGN_IN); //opens different gmail accounts to be signed in
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //why?

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);//gets the signed in account from the list of mails
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());//
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    //official firebase code..
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null); //gets the account credientials(email, pass maybe)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() { //as soon as this task is complete
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);

                            Users users = new Users(); //empty constructor
                            users.setUserId(user.getUid()); //sets the retrived user id
                            users.setUserName(user.getDisplayName()); //sets the retrieved username
                            users.setProfilePic(user.getPhotoUrl().toString()); //sets the retrieved photo url

                            // creates the child users and gets the user id and user information
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            // as soon all this is complete the user is signed in directed to main activity
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Signed In with Google",Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}