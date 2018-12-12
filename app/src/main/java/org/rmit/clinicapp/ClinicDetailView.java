package org.rmit.clinicapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ClinicDetailView extends AppCompatActivity {

    private static final String TAG = "";
    private TextView name;
    private TextView impression;
    private TextView lead;
    private TextView specialization;
    private TextView rating;
    private TextView lat;
    private TextView lon;
    private TextView average;
    private TextView address;
    private Geocoder geocoder;
    private List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_detail_view);
        name = findViewById(R.id.detailName);
        impression = findViewById(R.id.detailImpression);
        lead = findViewById(R.id.detailLead);
        specialization = findViewById(R.id.detailSpecialization);
        rating = findViewById(R.id.detailRating);
        lat = findViewById(R.id.detailLat);
        lon = findViewById(R.id.detailLon);
        average = findViewById(R.id.detailAVG);
        address = findViewById(R.id.detailAddress);
        String n = "";



//        intent.putExtra("sentName", clinics.get(id).name);
//        intent.putExtra("sentId", clinics.get(id).id);
//        intent.putExtra("sentRating", clinics.get(id).rating);
//        intent.putExtra("sentLat", clinics.get(id).latitute);
//        intent.putExtra("sentLon", clinics.get(id).longitute);
//        intent.putExtra("sentImpression", clinics.get(id).impression);
//        intent.putExtra("sentLead", clinics.get(id).lead_physician);
//        intent.putExtra("sentSpecialize", clinics.get(id).specialization);
//        intent.putExtra("sentAvgPrice", clinics.get(id).average_price);

        Intent intent = getIntent();
        name.setText(intent.getStringExtra("sentName"));
        impression.setText(intent.getStringExtra("sentImpression"));
        lead.setText(intent.getStringExtra("sentLead"));
        specialization.setText(intent.getStringExtra("sentSpecialize"));
        rating.setText(Integer.toString(intent.getIntExtra("sentRating",0)));


        Double latMe = intent.getDoubleExtra("sentLat",0);
        Double lonMe = intent.getDoubleExtra("sentLon",0);

        lat.setText(latMe.toString());
        lon.setText(lonMe.toString());
        average.setText(Integer.toString(intent.getIntExtra("sentAvgPrice",0)));

        geocoder = new Geocoder(this,Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latMe,lonMe,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.get(0).getAddressLine(0).length() >= 16){

            n = addresses.get(0).getAddressLine(0).replace(",","\n");
//            Log.d(TAG, "onCreate: " + n.length());
        }
//        address.setText(addresses.get(0).getAddressLine(0));
        address.setText(n);

    }

    public void exitFunc(View view) {
        finish();
    }
}
