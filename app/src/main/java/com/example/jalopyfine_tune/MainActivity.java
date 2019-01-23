package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    Button signup_btn,signin_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signin_btn=findViewById(R.id.SignInbtn);
        signup_btn=findViewById(R.id.SignUpbtn);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_signup=new Intent(MainActivity.this,SignUp.class);
                startActivity(i_signup);
            }
        });

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_signin=new Intent(MainActivity.this,SignIn.class);
                startActivity(i_signin);

            }
        });
    }
}
