package com.example.jalopyfine_tune;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class HistorySingleObject extends AppCompatActivity implements OnMapReadyCallback {
    private String workId, currentUserId, customerId, workerId, type, service;

    private TextView workLocation;
    private TextView workDate;
    private TextView userName;
    private TextView userPhone;

    private ImageView userImage;

    private RatingBar mRatingBar;

    private DatabaseReference historyWorkInfoDB;

    private LatLng workLatLng;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    String mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single_object);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);

        workId = getIntent().getExtras().getString("workId");

        workLocation = (TextView) findViewById(R.id.workLocation);
        workDate = (TextView) findViewById(R.id.workDate);
        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);

        userImage = (ImageView) findViewById(R.id.userImage);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRating = getIntent().getStringExtra("rating");

        historyWorkInfoDB = FirebaseDatabase.getInstance().getReference().child("history").child(workId);
        getWorkInformation();
    }

    private void getWorkInformation() {
        historyWorkInfoDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.getKey().equals("1workingType")){
                            type = child.getValue().toString();
                        }
                        if (child.getKey().equals("2workingService")){
                            service = child.getValue().toString();
                        }
                        if (child.getKey().equals("3worker")){
                            workerId = child.getValue().toString();
                            workCompleteRating(mRating);
                        }
                        if (child.getKey().equals("4customer")){
                            customerId = child.getValue().toString();
                            if (customerId.equals(currentUserId)){
                                Toast.makeText(HistorySingleObject.this, type + service + workerId, Toast.LENGTH_SHORT).show();
                                getUserInformation();
                                displayRatingObject();
                            }
                        }
                        if (child.getKey().equals("6timestamp")){
                            workDate.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals("7destination")) {
                            workLocation.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("8location")) {
                            workLatLng = new LatLng(Double.valueOf(child.child("lat").getValue().toString()), Double.valueOf(child.child("lng").getValue().toString()));
                            if (workLatLng != new LatLng(0, 0)) {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(workLatLng));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(workLatLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                            }
                        }
                        if (child.getKey().equals("9rating")){
                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void workCompleteRating(String rating){
        historyWorkInfoDB.child("9rating").setValue(rating);
        DatabaseReference mWorkerRatingDB = FirebaseDatabase.getInstance().getReference().child("Workers").child(type).child(service).child(workerId).child("rating");
        mWorkerRatingDB.child(workId).setValue(rating);
    }

    private void displayRatingObject() {
        mRatingBar.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                historyWorkInfoDB.child("9rating").setValue(rating);
                DatabaseReference mWorkerRatingDB = FirebaseDatabase.getInstance().getReference().child("Workers").child(type).child(service).child(workerId).child("rating");
                mWorkerRatingDB.child(workId).setValue(rating);
            }
        });
    }

    private void getUserInformation() {
        DatabaseReference infoDB = FirebaseDatabase.getInstance().getReference().child("Workers").child(type).child(service).child(workerId);
        infoDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map= (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null){
                        userName.setText(map.get("name").toString());
                    }
                    if (map.get("phone") != null){
                        userPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("profileImageUrl") != null){
                        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("worker_profile_Images").child(workerId);
                        filePath.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length);
                                userImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, userImage.getWidth(), userImage.getHeight(), false));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timeStamp * 1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();

        return date;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
