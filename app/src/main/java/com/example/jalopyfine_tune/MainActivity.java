package com.example.jalopyfine_tune;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TextView textView_email, textView_phone;

    LinearLayout emailLayout, phoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_email = (TextView) findViewById(R.id.withEmail);
        textView_phone = (TextView) findViewById(R.id.withPhone);

        emailLayout = (LinearLayout) findViewById(R.id.layout_email);
        phoneLayout = (LinearLayout) findViewById(R.id.layout_phone);


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

    }
}
