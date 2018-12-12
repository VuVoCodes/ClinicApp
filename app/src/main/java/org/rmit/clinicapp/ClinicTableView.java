package org.rmit.clinicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.rmit.clinicapp.MapsActivity.API_URL;

public class ClinicTableView extends AppCompatActivity {

    private String jsonString;
    private static final String TAG = "";
    private ListView listView;
    public ArrayList<Clinic> clinics;
    private boolean filteredBoo;
    private Button sortButton;
    private EditText sortEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_table_view);
        listView = findViewById(R.id.ListView);
        clinics = new ArrayList<Clinic>();
        sortButton = findViewById(R.id.sortMe);
        sortEditor = findViewById(R.id.editSortText);
        new GetClinic().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listView.invalidateViews();
        new GetClinic().execute();
    }
    
    private class GetClinic extends AsyncTask<Void, Void, Void> {
        String jsonString = "";

        @Override
        protected Void doInBackground(Void... voids) {
            jsonString = HttpHandler.getRequest(API_URL);
            Log.d(TAG, "doInBackground: " + jsonString);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final ArrayList<String> names = new ArrayList<String>();
            super.onPostExecute(aVoid);
            clinics.clear();
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("_id");
                    String name = jsonObject.getString("name");
                    int rating = jsonObject.getInt("rating");
                    Double lat = jsonObject.getDouble("latitute");
                    Double lon = jsonObject.getDouble("longitute");
                    String impression = jsonObject.getString("impression");
                    String lead = jsonObject.getString("lead_physician");
                    String specialization = jsonObject.getString("specialization");
                    int averagePrice = jsonObject.getInt("average_price");
                    Log.d(TAG, "onPostExecute: " + jsonArray.toString());

                    Clinic clinic = new Clinic();
                    clinic.id = id;
                    clinic.name = name;
                    clinic.rating = rating;

                    clinic.latitute = lat;
                    clinic.longitute = lon;
                    clinic.impression = impression;
                    clinic.lead_physician = lead;
                    clinic.specialization = specialization;
                    clinic.average_price = averagePrice;
                    clinics.add(clinic);

                    CustomListView customListView = new CustomListView(ClinicTableView.this, clinics);
                    customListView.notifyDataSetChanged();
                    listView.setAdapter(customListView);
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            ShowUpMenuActivity showUpMenuActivity = new ShowUpMenuActivity(ClinicTableView.this, position);
                            showUpMenuActivity.showPopup(view);
                            return true;
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ClinicTableView.this,ClinicDetailView.class);
                            intent.putExtra("sentName", clinics.get(position).name);
                            intent.putExtra("sentId", clinics.get(position).id);
                            intent.putExtra("sentRating", clinics.get(position).rating);
                            intent.putExtra("sentLat", clinics.get(position).latitute);
                            intent.putExtra("sentLon", clinics.get(position).longitute);
                            intent.putExtra("sentImpression", clinics.get(position).impression);
                            intent.putExtra("sentLead", clinics.get(position).lead_physician);
                            intent.putExtra("sentSpecialize", clinics.get(position).specialization);
                            intent.putExtra("sentAvgPrice", clinics.get(position).average_price);
                            intent.putExtra("requestType", "detailView");
                            startActivityForResult(intent, 2);
                        }
                    });
                    filteredBoo = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class ShowUpMenuActivity implements PopupMenu.OnMenuItemClickListener {

        private Activity context;
        private Integer clinicID;

        ShowUpMenuActivity(Activity context, Integer clinicID) {
            this.context = context;
            this.clinicID = clinicID;
        }

        void showPopup(View v) {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.popup_layout);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    DeleteClinic(clinicID);
                    clinics.clear();
                    listView.invalidateViews();
                    new GetClinic().execute();
                    Toast.makeText(context, "item has been deleted", Toast.LENGTH_LONG).show();
                    break;
                case R.id.edit:
                    editClinic(clinicID);
                    break;
                case R.id.map:
                    Toast.makeText(context, clinics.get(clinicID).name, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClinicTableView.this, MapsActivity.class);
                    intent.putExtra("lat", clinics.get(clinicID).latitute);
                    intent.putExtra("lon", clinics.get(clinicID).longitute);
                    intent.putExtra("requestType", "mapPosition");
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
            return false;
        }
    }

    public void editClinic(int id) {
        Intent intent = new Intent(this, AddEditClinicActivity.class);
        intent.putExtra("sentName", clinics.get(id).name);
        intent.putExtra("sentId", clinics.get(id).id);
        intent.putExtra("sentRating", clinics.get(id).rating);
        intent.putExtra("sentLat", clinics.get(id).latitute);
        intent.putExtra("sentLon", clinics.get(id).longitute);
        intent.putExtra("sentImpression", clinics.get(id).impression);
        intent.putExtra("sentLead", clinics.get(id).lead_physician);
        intent.putExtra("sentSpecialize", clinics.get(id).specialization);
        intent.putExtra("sentAvgPrice", clinics.get(id).average_price);
        intent.putExtra("requestType", "edit");
        startActivityForResult(intent, 1);
    }

    private class DeleteClinic extends AsyncTask<Void, Void, Void> {
        String jsonString = "";
        String clinicID = "";

        @Override
        protected Void doInBackground(Void... voids) {
            jsonString = HttpHandler.DeleteClinicRequest(API_URL + "/" + clinicID);
            Log.d(TAG, "doInBackground: " + jsonString);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final ArrayList<String> names = new ArrayList<String>();
            super.onPostExecute(aVoid);
        }
    }

    public void DeleteClinic(int id) {
        DeleteClinic deleteClinic = new DeleteClinic();
        deleteClinic.clinicID = clinics.get(id).id;
        deleteClinic.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String returnID = (String) data.getExtras().get("returnId");
                String returnName = (String) data.getExtras().get("returnName");
                Integer returnRating = data.getIntExtra("returnRating", 0);
                Double returnLat = data.getDoubleExtra("returnLat", 0);
                Double returnLon = data.getDoubleExtra("returnLon", 0);
                String returnImpression = data.getStringExtra("returnImpression");
                String returnLead = data.getStringExtra("returnLead");
                String returnSpecialzie = data.getStringExtra("returnSpecialize");
                Integer returnAverage = data.getIntExtra("returnAvgPrice", 0);

                Log.d(TAG, "onActivityResult: " + returnID);
            }
        }
    }

    public void Sorting(View view) {
        if(!filteredBoo){
            String sortText = sortEditor.getText().toString();
            SortingIt(sortText);
            sortButton.setText("Cancel Sort " + sortText);
            sortEditor.setEnabled(false);
        }else{
            CustomListViewGenerator(clinics);
            sortButton.setText("sort");
            filteredBoo = false;
            sortEditor.setEnabled(true);
            sortEditor.setText("");
        }
    }

    
    public void SortingIt(String filterString) {
        ArrayList<Clinic> sortedClinics = new ArrayList<>();
        if (clinics.size() < 0) {
            Log.d(TAG, "Sorting: Fetching");
        } else {
            for (Clinic clinicMe : clinics) {
                if (clinicMe.specialization.toLowerCase().matches(filterString.toLowerCase())) {
                    sortedClinics.add(clinicMe);
                }
            }
           CustomListViewGenerator(sortedClinics);
            filteredBoo = true;
        }
    }

    public void CustomListViewGenerator(ArrayList<Clinic> insertedArray){
        CustomListView customListView = new CustomListView(ClinicTableView.this, insertedArray);
        customListView.notifyDataSetChanged();
        listView.setAdapter(customListView);
        listView.invalidateViews();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ShowUpMenuActivity showUpMenuActivity = new ShowUpMenuActivity(ClinicTableView.this, position);
                showUpMenuActivity.showPopup(view);
                return true;
            }
        });
    }
}
