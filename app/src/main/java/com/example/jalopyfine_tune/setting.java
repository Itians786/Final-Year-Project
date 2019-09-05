package com.example.jalopyfine_tune;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setting extends AppCompatActivity {

    ProgressDialog progressDialog;

    private ImageView imgView;
    private TextView txt_name, txt_mobile, txt_email;
    private Button btn_name, btn_mobile, btn_email, btn_changePic;

    //AlertDialog
    View promptsView;
    private TextView txt_info;
    private EditText et_newInfoName;
    private EditText et_newInfoPhone;
    private EditText et_newInfoEmail;
    AlertDialog.Builder alertDialogBuilderName;
    AlertDialog.Builder alertDialogBuilderPhone;
    AlertDialog.Builder alertDialogBuilderEmail;

    private String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private String mName, mPhone, mEmail, mProfileImageUrl;

    private Uri resultUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        imgView = (ImageView) findViewById(R.id.settings_imageView);

        txt_name = (TextView) findViewById(R.id.e_name);
        txt_mobile = (TextView) findViewById(R.id.e_mobile);
        txt_email = (TextView) findViewById(R.id.e_mail);

        btn_name = (Button) findViewById(R.id.edit_n);
        btn_mobile = (Button) findViewById(R.id.edit_m);
        btn_email = (Button) findViewById(R.id.edit_e);
        btn_changePic = (Button) findViewById(R.id.changePic);

        //txt_info = (TextView) findViewById(R.id.txt_info);
        //et_newInfo = (EditText) findViewById(R.id.et_newInfo);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Customers").child(userID);

        et_newInfoName = new EditText(setting.this);
        et_newInfoEmail = new EditText(setting.this);
        et_newInfoPhone = new EditText(setting.this);

        getUserInfo();

        btn_changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePicture();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        btn_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uName = txt_name.getText().toString();

                fetchName(uName);
            }
        });

        btn_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uPhone = txt_mobile.getText().toString();

                fetchPhone(uPhone);
            }
        });

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uEmail = txt_email.getText().toString();

                fetchMail(uEmail);
            }
        });
    }



    private void getUserInfo() {
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mName = map.get("name").toString();
                        txt_name.setText(mName);
                    }
                    if (map.get("phone") != null) {
                        mPhone = map.get("phone").toString();
                        txt_mobile.setText(mPhone);
                    }
                    if (map.get("email") != null) {
                        mEmail = map.get("email").toString();
                        txt_email.setText(mEmail);
                    }
                    if (map.get("profileImageUrl") != null) {
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_Images").child(userID);
                        filePath.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imgView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imgView.getWidth(), imgView.getHeight(), false));
                            }
                        });
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;

            imgView.setImageURI(resultUri);
        }
    }

    private void fetchName(String uName) {
        alertDialogBuilderName = new AlertDialog.Builder(setting.this);
        alertDialogBuilderName.setView(et_newInfoName);

        alertDialogBuilderName
                .setTitle("Change name")
                .setMessage(uName)
                .setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txt_name.setText(et_newInfoName.getText().toString());
                        Map data = new HashMap();
                        data.put("name", et_newInfoName.getText().toString());

                        mCustomerDatabase.updateChildren(data);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilderName.create();
        alertDialog.show();
    }

    private void fetchPhone(String uPhone) {
        alertDialogBuilderPhone = new AlertDialog.Builder(setting.this);
        alertDialogBuilderPhone.setView(et_newInfoPhone);

        alertDialogBuilderPhone
                .setTitle("Change mobile number")
                .setMessage(uPhone)
                .setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txt_name.setText(et_newInfoPhone.getText().toString());
                        Map data = new HashMap();
                        data.put("name", et_newInfoPhone.getText().toString());

                        mCustomerDatabase.updateChildren(data);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilderPhone.create();
        alertDialog.show();
    }

    private void fetchMail(String uMail) {
        alertDialogBuilderEmail= new AlertDialog.Builder(setting.this);
        alertDialogBuilderEmail.setView(et_newInfoEmail);

        alertDialogBuilderEmail
                .setTitle("Change email")
                .setMessage(uMail)
                .setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txt_name.setText(et_newInfoEmail.getText().toString());
                        Map data = new HashMap();
                        data.put("name", et_newInfoEmail.getText().toString());

                        mCustomerDatabase.updateChildren(data);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilderEmail.create();
        alertDialog.show();
    }

    public void changePicture(){
        if (resultUri != null){
            final ProgressDialog progDialog = new ProgressDialog(this);
            progDialog.setTitle("Uploading...");
            progDialog.show();

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_Images").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progDialog.dismiss();
                        Uri downloadUri = task.getResult();

                        if (downloadUri == null) {
                            Toast.makeText(setting.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", downloadUri.toString());
                            mCustomerDatabase.updateChildren(newImage);
                        }
                    }}}
                );
            }

        }
    }
