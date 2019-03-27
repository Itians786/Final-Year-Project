package com.example.jalopyfine_tune;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class BikePetrol extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    Button request, cancel_request;
    private LatLng customerLocation;

    Boolean requestBol = false;

    private Marker mMarker;

    ArrayList<LatLng> listPoints;

    private LinearLayout workerInfo;

    private ImageView workerProfileImg;

    private TextView workerName, workerPhone;

    private RatingBar mRatingBar;

    //For rate the worker when Works Complete
    private LinearLayout mRateWorkerLayout;
    private TextView mRateWorkerTxt;
    private RatingBar mRateWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_petrol);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        workerInfo = (LinearLayout) findViewById(R.id.workerInfo);

        workerProfileImg = (ImageView) findViewById(R.id.workerProfileImg);

        workerName = (TextView) findViewById(R.id.workerName);
        workerPhone = (TextView) findViewById(R.id.workerPhone);

        request = (Button) findViewById(R.id.request);
        cancel_request = (Button) findViewById(R.id.cancel_request);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        //For rate the worker when Works Complete
        mRateWorkerLayout = (LinearLayout) findViewById(R.id.rateWorkerLayout);
        mRateWorkerTxt = (TextView) findViewById(R.id.rateWorkerTxt);
        mRateWorker = (RatingBar) findViewById(R.id.rateWorker);


        listPoints = new ArrayList<>();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBol = true;
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                customerLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMarker = mMap.addMarker(new MarkerOptions().position(customerLocation).title("I'm Here"));

                getClosestWorker();
            }

        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endWork();
            }
        });

    }

    private int radius = 1;
    private boolean workerFound = false;
    private String workerFoundID;
    GeoQuery geoQuery;

    private void getClosestWorker() {
        DatabaseReference workerAvailable = FirebaseDatabase.getInstance().getReference().child("workerAvailable").child("Bike").child("Petrol");

        GeoFire geoFire = new GeoFire(workerAvailable);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(customerLocation.latitude, customerLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!workerFound && requestBol) {
                    workerFound = true;
                    workerFoundID = key;

                    final DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference().child("Workers").child("Bike").child("Petrol").child(workerFoundID);
                    final String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap hashMap = new HashMap();
                    hashMap.put("CustomerStatusId", customerId);
                    workerRef.updateChildren(hashMap);

                    getWorkerLocation();
                    getWorkerInfo();
                    getHasWorkEnded();
                    request.setText("Looking for available workers . . . .");
                }
            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
                if (!workerFound) {
                    if (radius == 5) {
                        request.setText("No workers available");
                        requestBol = false;

                        return;
                    } else {
                        radius++;
                        getClosestWorker();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });

    }

    private void getWorkerInfo() {
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Workers").child("Bike").child("Petrol").child(workerFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    workerInfo.setVisibility(View.VISIBLE);

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        workerName.setText(map.get("name").toString());
                    }
                    if (map.get("phone") != null) {
                        workerPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("profileImageUrl") != null) {

                        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("worker_profile_Images").child(workerFoundID);
                        filePath.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                workerProfileImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, workerProfileImg.getWidth(), workerProfileImg.getHeight(), false));
                            }
                        });
                    }
                    int ratingSum = 0;
                    float ratingTotal = 0;
                    float ratingAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("8rating").getChildren()) {
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingTotal++;
                    }
                    if (ratingTotal != 0) {
                        ratingAvg = ratingSum / ratingTotal;
                        mRatingBar.setRating(ratingAvg);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference workHasEndedRef;
    private ValueEventListener workHasEndedRefListener;

    private void getHasWorkEnded() {
        workHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Workers").child("Bike").child("Petrol").child(workerFoundID).child("CustomerStatusId");
        workHasEndedRefListener = workHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("Customers").child(userId).child("history");
                    historyRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            String key = dataSnapshot.getKey().toString();
                            showRatingDialog(key);
                            endWork();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showRatingDialog(final String key) {
        mRateWorkerLayout.setVisibility(View.VISIBLE);

        final String workId = key;
        mRateWorker.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                DatabaseReference historyWorkInfoDB = FirebaseDatabase.getInstance().getReference().child("history").child(workId);
                historyWorkInfoDB.child("8rating").setValue(rating);

                Intent intent = new Intent(BikePetrol.this, HistorySingleObject.class);
                intent.putExtra("workId", workId);
                startActivity(intent);

                mRateWorkerLayout.setVisibility(View.GONE);
            }
        });
    }

    private void endWork() {
        requestBol = false;
        if (geoQuery != null) {
            geoQuery.removeAllListeners();
        }

        if (workerLocationRef != null) {
            workerLocationRef.removeEventListener(workerLocationRefListener);
            workHasEndedRef.removeEventListener(workHasEndedRefListener);
            marker.remove();
        }

        if (workerFoundID != null) {
            DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference().child("Workers").child("Bike").child("Petrol").child(workerFoundID).child("CustomerStatusId");
            workerRef.removeValue();
            workerFoundID = null;
        }

        workerFound = false;
        radius = 1;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (mMarker != null) {
            mMarker.remove();
        }
        request.setText("Find Worker");
        cancel_request.setVisibility(View.GONE);

        workerInfo.setVisibility(View.GONE);
    }


    private Marker marker;
    private DatabaseReference workerLocationRef;
    private ValueEventListener workerLocationRefListener;

    private void getWorkerLocation() {
        cancel_request.setVisibility(View.VISIBLE);

        workerLocationRef = FirebaseDatabase.getInstance().getReference().child("WorkersInWorking").child(workerFoundID).child("l");
        workerLocationRefListener = workerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//This is called at every location change in seconds for the worker
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;

                    request.setText("Worker Found");

                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }

                    LatLng workerLatLng = new LatLng(locationLat, locationLng);
                    if (marker != null) {
                        marker.remove();
                    }
                    Location location1 = new Location("");
                    location1.setLatitude(customerLocation.latitude);
                    location1.setLongitude(customerLocation.longitude);

                    Location location2 = new Location("");
                    location2.setLatitude(workerLatLng.latitude);
                    location2.setLongitude(workerLatLng.longitude);

                    float distance = location1.distanceTo(location2) / 1000;

                    if (distance < 1 / 10) {
                        request.setText("Worker Arrived");
                    } else {
                        request.setText("Distance: " + String.valueOf(distance) + "   km");
                    }

                    marker = mMap.addMarker(new MarkerOptions().position(workerLatLng).title("Your Worker"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(12 * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                checkLocationPermission();
            }
        }
    }

    //Permission Check

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
            }
        }
    };

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Need Permissions")
                        .setMessage("For Using this App you need to allow location access permission.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(BikePetrol.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            } else {
                ActivityCompat.requestPermissions(BikePetrol.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mRateWorkerLayout.getVisibility() == View.VISIBLE) {
            mRateWorker.setFocusable(true);
            mRateWorkerTxt.setError("");
        } else {
            super.onBackPressed();
        }

    }
}