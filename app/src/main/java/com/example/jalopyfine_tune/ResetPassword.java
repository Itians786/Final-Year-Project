package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    EditText et_email;
    Button btn_changePwd;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        et_email = (EditText) findViewById(R.id.pwdResetEmail);
        btn_changePwd = (Button) findViewById(R.id.changePwd);

        mAuth = FirebaseAuth.getInstance();

        btn_changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = et_email.getText().toString();

                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPassword.this, "Please write your valid email address", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPassword.this, "Check your email for password reset", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassword.this, PhoneSignIn.class));
                            }
                            else {
                                Toast.makeText(ResetPassword.this, "Error Occurred" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
