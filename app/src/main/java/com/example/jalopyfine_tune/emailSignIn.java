package com.example.jalopyfine_tune;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class emailSignIn extends Fragment {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    EditText et_email, et_password;
    Button btn_signIn,btn_signUp;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener fireBaseAuthListener;

    public emailSignIn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_email_sign_in, container, false);

        et_email = rootView.findViewById(R.id.Email_Signin);
        et_password = rootView.findViewById(R.id.Password_Signin);
        btn_signIn = rootView.findViewById(R.id.SignInbtn);
        btn_signUp= rootView.findViewById(R.id.SignUpbtn);
        FirebaseApp.initializeApp(getActivity());

        mAuth = FirebaseAuth.getInstance();

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),SignUp.class);
                startActivity(i);
            }
        });

        //AuthListener to check user is already logged in or not
        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){

                    Intent intent = new Intent(getActivity(), Navigation.class);
                    startActivity(intent);
                    return;
                }
            }
        };

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();
                if(email.isEmpty()||password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter Correct Email or Password", Toast.LENGTH_SHORT).show();
                }

                else {
                    //Authentication method for Login
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Login Error", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getActivity(), "Login Succesfully", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireBaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireBaseAuthListener);
    }

}
