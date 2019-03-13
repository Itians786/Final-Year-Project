package com.example.jalopyfine_tune;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class BikeMechanic extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    Button request, cancel_request;
    private LatLng customerLocation;

    Boolean requestBol = false;

    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_mechanic);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        request = findViewById(R.id.request);
        cancel_request = findViewById(R.id.cancel_request);

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

                request.setText("Getting free worker . . . .");

                getClosestWorker();
            }

        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBol = false;
                if (geoQuery != null) {
                    geoQuery.removeAllListeners();
                }

                if (workerLocationRef != null) {
                    workerLocationRef.removeEventListener(workerLocationRefListener);
                    marker.remove();
                }

                if (workerFoundID != null) {
                    DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference().child("Workers").child("Bike").child("Mechanic").child(workerFoundID);
                    workerRef.setValue(true);
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
            }
        });

    }

    private int radius = 1;
    private boolean workerFound = false;
    private String workerFoundID;
    GeoQuery geoQuery;

    private void getClosestWorker() {
        DatabaseReference workerAvailable = FirebaseDatabase.getInstance().getReference().child("workerAvailable").child("Bike").child("Mechanic");

        GeoFire geoFire = new GeoFire(workerAvailable);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(customerLocation.latitude, customerLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!workerFound && requestBol) {
                    workerFound = true;
                    workerFoundID = key;

                    final DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference().child("Workers").child("Bike").child("Mechanic").child(workerFoundID);
                    final String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap hashMap = new HashMap();
                    hashMap.put("CustomerStatusId", customerId);
                    workerRef.updateChildren(hashMap);

                    getWorkerLocation();
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

    private Marker marker;
    private DatabaseReference workerLocationRef;
    private ValueEventListener workerLocationRefListener;

    private void getWorkerLocation() {
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

                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


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
                                ActivityCompat.requestPermissions(BikeMechanic.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            } else {
                ActivityCompat.requestPermissions(BikeMechanic.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
}