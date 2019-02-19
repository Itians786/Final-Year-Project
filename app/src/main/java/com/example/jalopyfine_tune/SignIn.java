package com.example.jalopyfine_tune;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    EditText email_signin, password_signin;
    Button signinbtn,signupbtn;

    FirebaseAuth mmAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email_signin = findViewById(R.id.Email_Signin);
        password_signin = findViewById(R.id.Password_Signin);
        signinbtn = findViewById(R.id.SignInbtn);
        signupbtn=findViewById(R.id.SignUpbtn);
        FirebaseApp.initializeApp(this);

        mmAuth = FirebaseAuth.getInstance();

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SignIn.this,SignUp.class);
                startActivity(i);

            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){

                    Intent intent = new Intent(SignIn.this, Navigation.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_signin.getText().toString();
                final String password = password_signin.getText().toString();
                if(email.isEmpty()||password.isEmpty()){
                    Toast.makeText(SignIn.this, "Enter Correct Email or Password", Toast.LENGTH_SHORT).show();
                }

                else {
                mmAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Login Error", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(SignIn.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mmAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mmAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
