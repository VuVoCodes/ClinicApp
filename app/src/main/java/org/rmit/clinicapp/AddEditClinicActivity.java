package org.rmit.clinicapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddEditClinicActivity extends AppCompatActivity {


    private static final String TAG = "";

    private Geocoder geocoder;
    private Clinic clinic;
    private List<Address> addresses;
    private String address;
    private TextView titleView;
    private EditText editName;
    private EditText editAddress;
    private EditText editRating;
    private EditText editImpression;
    private EditText editLead;
    private EditText editSpecialization;
    private EditText editAveragePrice;
    private String requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_clinic);
        Intent intent = getIntent();
        titleView = findViewById(R.id.titleView);
        editName = findViewById(R.id.name);
        editAddress = findViewById(R.id.address);
        editRating = findViewById(R.id.rating);
        editImpression = findViewById(R.id.impression);
        editLead = findViewById(R.id.leadphysic);
        editSpecialization = findViewById(R.id.specialization);
        editAveragePrice = findViewById(R.id.averagePrice);
        editAddress.setText(address);
        editAddress.setEnabled(false);

        if(intent.getExtras().get("requestType").toString().matches("add")){
            clinic = new Clinic();
            clinic.latitute = intent.getDoubleExtra("latitude",0);
            clinic.longitute = intent.getDoubleExtra("longitude",0);
            requestType = "add";

            geocoder = new Geocoder(this,Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(clinic.latitute,clinic.longitute,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            address = addresses.get(0).getAddressLine(0);

            editAddress.setText(address);
            titleView.setText("ADD CLINIC");
        }else{

            clinic = new Clinic();
            String name = intent.getExtras().get("sentName").toString();
            String id = intent.getExtras().get("sentId").toString();
            Integer rating = intent.getIntExtra("sentRating",0);
            Double lat = intent.getDoubleExtra("sentLat",0);
            Double lon = intent.getDoubleExtra("sentLon",0);
            String impression = intent.getExtras().get("sentImpression").toString();
            String lead = intent.getExtras().get("sentLead").toString();
            String specialzation = intent.getExtras().get("sentSpecialize").toString();
            Integer avgPrice = intent.getIntExtra("sentAvgPrice",0);

            geocoder = new Geocoder(this,Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lat,lon,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            address = addresses.get(0).getAddressLine(0);

            editName.setText(name);
            editAddress.setText(address);
            editRating.setText(rating.toString());
            editImpression.setText(impression);
            editLead.setText(lead);
            editSpecialization.setText(specialzation);
            editAveragePrice.setText(avgPrice.toString());
            titleView.setText("EDIT CLINIC");
            requestType = "edit";

            clinic.latitute = lat;
            clinic.longitute = lon;
            clinic.id = id;
        }

    }


    public void confirmAction(View view) {
        clinic.name = editName.getText().toString();
        clinic.rating = Integer.parseInt(editRating.getText().toString());
        clinic.impression = editImpression.getText().toString();
        clinic.average_price = Integer.parseInt(editAveragePrice.getText().toString());
        clinic.lead_physician = editLead.getText().toString();
        clinic.specialization = editSpecialization.getText().toString();
        if(requestType.matches("add")){
            new PostClinic().execute();
        }else{
            new PutClinic().execute();
        }
    }


    private class PostClinic extends AsyncTask<Void,Void,Void>{
        private String status = "";
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.postClinicRequest(MapsActivity.API_URL,clinic);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(AddEditClinicActivity.this, status, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddEditClinicActivity.this,
                    MapsActivity.class);
            setResult(101, intent);
            finish();
        }
    }

    private class PutClinic extends AsyncTask<Void,Void,Void> {
        private String status = "";
        @Override
        protected Void doInBackground(Void... voids) {
            status = HttpHandler.putClinicRequest(MapsActivity.API_URL + "/" + clinic.id, clinic);
            Log.d(TAG, "doInBackground: " + MapsActivity.API_URL + "/" + clinic.id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(AddEditClinicActivity.this, status, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddEditClinicActivity.this,
                    MapsActivity.class);
            intent.putExtra("returnId",clinic.id);
            intent.putExtra("returnName",clinic.name);
            intent.putExtra("returnRating",clinic.rating);
            intent.putExtra("returnLat",clinic.latitute);
            intent.putExtra("returnLon",clinic.longitute);
            intent.putExtra("returnImpression",clinic.impression);
            intent.putExtra("returnLead",clinic.lead_physician);
            intent.putExtra("returnSpecialize",clinic.specialization);
            intent.putExtra("returnAvgPrice",clinic.average_price);
//           intent.putStringArrayListExtra("lll",clinic.specialization)
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
