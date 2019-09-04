package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private EditText et_phoneSignup, et_nameSingup, et_emailSignup, et_passwordSignup, et_confirmpwdSignup;
    private String phone, name, email, password, confirmpassword;
    Button signupbtn2, sign_in;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_phoneSignup = findViewById(R.id.Phone_SignUp);
        et_nameSingup = findViewById(R.id.Name_SignUp);
        et_emailSignup = findViewById(R.id.Email_SignUp);
        et_passwordSignup = findViewById(R.id.Password_SignUp);
        et_confirmpwdSignup = findViewById(R.id.Confirm_Password);
        signupbtn2 = findViewById(R.id.SignUpbtn2);
        sign_in = findViewById(R.id.signIn);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    Intent intent = new Intent(SignUp.this, Navigation.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SignUp.this, PhoneSignIn.class);
                startActivity(in);
            }
        });
        signupbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = et_phoneSignup.getText().toString();
                name = et_nameSingup.getText().toString();
                email = et_emailSignup.getText().toString();
                password = et_passwordSignup.getText().toString();
                confirmpassword = et_confirmpwdSignup.getText().toString();

                if (!validate()) {
                    Toast.makeText(SignUp.this, "This SignUp has been failed", Toast.LENGTH_LONG).show();
                }// call when the button clicked to validate the input

                else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "SignUp Error", Toast.LENGTH_SHORT).show();
                            } else {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Customers").child(user_id);
                                current_user_db.setValue(true);
                                Toast.makeText(SignUp.this, "SignUp Success", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }


    public boolean validate() {
        boolean valid = true;

        if (phone.isEmpty()) {
            et_phoneSignup.setError("Enter phone number");
            valid = false;
        }

        if (name.isEmpty() || name.length() > 32) {
            et_nameSingup.setError("please correct the name");
            valid = false;

        }

        if (email.isEmpty()) {
            et_emailSignup.setError("please enter ");
            valid = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_emailSignup.setError("please enter valid e-email adress");
            valid = false;
        }

        if (password.isEmpty()) {
            et_passwordSignup.setError("Enter password");
            valid = false;
        }

        if (confirmpassword.isEmpty()) {
            et_confirmpwdSignup.setError("Enter confirm");
            valid = false;
        }
        else if (!confirmpassword.equals(password)) {

            et_confirmpwdSignup.setError("Not Match");
            valid = false;
        }

        return valid;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
