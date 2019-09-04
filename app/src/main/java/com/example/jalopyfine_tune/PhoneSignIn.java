package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneSignIn extends AppCompatActivity {

    TextView textView_email, textView_phone;

    LinearLayout emailLayout, phoneLayout;

    EditText phoneNumber, verificationCode;

    Button requestCode, mLogin;

    FirebaseAuth mAuth;

    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_in);

        mAuth = FirebaseAuth.getInstance();

        textView_email = (TextView) findViewById(R.id.withEmail);
        textView_phone = (TextView) findViewById(R.id.withPhone);

        emailLayout = (LinearLayout) findViewById(R.id.layout_email);
        phoneLayout = (LinearLayout) findViewById(R.id.layout_phone);

        phoneNumber = findViewById(R.id.phone_number);
        verificationCode = findViewById(R.id.code);
        requestCode = findViewById(R.id.request_code);
        mLogin = findViewById(R.id.login);

        textView_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLayout.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.GONE);


            }
        });

        textView_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneLayout.setVisibility(View.VISIBLE);
                emailLayout.setVisibility(View.GONE);
            }
        });

        requestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });
    }

    private void verifyCode() {
        String code = verificationCode.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(PhoneSignIn.this, Navigation.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneSignIn.this, "Verification code not matched", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {
        String phone = phoneNumber.getText().toString();

        if (phone.isEmpty()) {
            phoneNumber.setError("Phone Number required");
            phoneNumber.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };
}
