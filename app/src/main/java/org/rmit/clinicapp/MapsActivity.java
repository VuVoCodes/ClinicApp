package org.rmit.clinicapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

//    public static final String API_URL = "https://vu-nodejs-backend-webprog-a2.herokuapp.com/students";
    public static final String API_URL = "https://clinicandroidasn2.herokuapp.com/clinics";

    private static final int MY_LOCATION_REQUEST = 99;
    private static final String TAG = "";
    private GoogleMap mMap;
    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;
    private ArrayList<Clinic> clinicArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try{
            MapsInitializer.initialize(getApplicationContext());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestPermission();
        locationClient = LocationServices.
                getFusedLocationProviderClient(this);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,12));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(MapsActivity.this,
                        AddEditClinicActivity.class);
                intent.putExtra("latitude", latLng.latitude);
                intent.putExtra("longitude", latLng.longitude);
                intent.putExtra("requestType","add");
                startActivity(intent);
            }
        });
        startLocationUpdate();
        new GetClinic().execute();

    }


    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void startLocationUpdate() {
//        locationRequest = new LocationRequest();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); //10s
        locationRequest.setFastestInterval(2000); //2s
        locationClient.requestLocationUpdates(locationRequest,
                new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location location = locationResult.getLastLocation();
                        LatLng latLng = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //  mMap.clear();
//                        mMap.addMarker(new MarkerOptions().position(latLng)
//                                        .icon(BitmapDescriptorFactory.defaultMarker()));
                        Toast.makeText(MapsActivity.this,
                                "(" + location.getLatitude() + ","+
                                        location.getLongitude() +")",
                                Toast.LENGTH_SHORT).show();

                    }
                }
                ,null);

    }

    @SuppressLint("MissingPermission")
    public void onGetPositionClick(View view) {

        locationClient.getLastLocation().
                addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            Toast.makeText(MapsActivity.this,
                                    "(" + location.getLatitude() + ","+
                                            location.getLongitude() +")",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MapsActivity.this, "Pls wait", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MapsActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //mMap.clear();
        new GetClinic().execute();
    }

    public void onViewAll(View view) {
        Intent intent = new Intent(MapsActivity.this, ClinicTableView.class);
        startActivity(intent);

    }

    private class GetClinic extends AsyncTask<Void,Void,Void>{
        String jsonString="";

        @Override
        protected Void doInBackground(Void... voids) {
            jsonString = HttpHandler.getRequest(API_URL);
            Log.d(TAG, "doInBackground: " + jsonString);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<Clinic> clinics = new ArrayList<Clinic>();
            super.onPostExecute(aVoid);
            try {
                MapsInitializer.initialize(getApplicationContext());
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    LatLng position = new LatLng(
                            jsonObject.getDouble("latitute"),
                            jsonObject.getDouble("longitute")
                    );
                    mMap.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.fromResource(
                                    R.drawable.ic_action_name
                            ))
                            .title("Clinic")
                            .snippet(jsonObject.getString("name")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
