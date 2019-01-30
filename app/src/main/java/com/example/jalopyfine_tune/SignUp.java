package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private EditText  et_phoneSignup, et_nameSingup, et_emailSignup, et_passwordSignup, et_confirmpwdSignup;
    private  String   phone, name, email,password , confirmpassword;
    Button signupbtn2,pra;


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
        pra=findViewById(R.id.button);

        pra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p=new Intent(SignUp.this,Navigation.class);
                startActivity(p);
            }
        });

        signupbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(); // call when the button clicked to validate the input
            }
        });
    }

        public void register ()
        {
            initialize();  // initialize the input variable to string value
            if (!validate()) {
                Toast.makeText(this, "this signup has been failed", Toast.LENGTH_LONG).show();
            } else {
                onSinUpSuccess();
            }
        }

        public void onSinUpSuccess ()
        {
            Toast.makeText(this, "we have signed up successfully", Toast.LENGTH_SHORT).show();

        }


        public boolean validate ()
        {
            boolean valid=true;


            if


            (phone.isEmpty()) {
                et_phoneSignup.setError("Enter phone number");
                valid=false;
            }


            if (name.isEmpty() || name.length() > 32) {
                et_nameSingup.setError("please correct the name");
                valid = false;

            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) ;
            {
                et_emailSignup.setError("please enter valid te email adress");
                valid = false;
            }

            if (password.isEmpty()) {
                et_passwordSignup.setError("Enter password");
                valid = false;
            }


            if (confirmpassword.isEmpty()) {
                et_confirmpwdSignup.setError("Enter phone number");
                valid = false;
            }
            return valid;


        }

        public void initialize ()
        {
            phone = et_phoneSignup.getText().toString().trim();
            name = et_nameSingup.getText().toString().trim();
            email = et_emailSignup.getText().toString().trim();
            password = et_passwordSignup.getText().toString().trim();
            confirmpassword = et_confirmpwdSignup.getText().toString().trim();
        }

}
