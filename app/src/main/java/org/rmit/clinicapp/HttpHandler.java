package org.rmit.clinicapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.google.android.gms.wearable.DataMap.TAG;

public class HttpHandler {
    public static String getRequest(String urlStr){
        StringBuilder stringBuilder = new StringBuilder();

        try {
            //Step 1: connect to the web service
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)
                    url.openConnection();
            // Step 2: read the output from the server
            InputStreamReader inputStreamReader =
                    new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader
            );
            String line ="";
            while ((line = bufferedReader.readLine())!= null){
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static String postClinicRequest(String urlStr, Clinic clinic){
        String status = "";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)
                    url.openConnection();
            //Step 2: define the request
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            //step 3: prepare the post data
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", clinic.name);
            jsonObject.put("rating", clinic.rating);
            jsonObject.put("latitute", clinic.latitute);
            jsonObject.put("longitute", clinic.longitute);
            jsonObject.put("impression", clinic.impression);
            jsonObject.put("lead_physician", clinic.lead_physician);
            jsonObject.put("specialization",clinic.specialization);
            jsonObject.put("average_price",clinic.average_price);
            //step 4: send json to the webservice
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObject.toString());
            os.flush();
            status = conn.getResponseCode() + ": " + conn.getResponseMessage();
            os.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String DeleteClinicRequest(String urlStr){
        String status = "";
        try{
            URL url = new URL(urlStr);
            Log.d(TAG, "DeleteClinicRequest: " + urlStr);
            HttpURLConnection conn = (HttpURLConnection)
                    url.openConnection();
            //Step 2: define the request
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type","application/json");
            status = conn.getResponseCode() + ": " + conn.getResponseMessage();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String putClinicRequest(String urlStr, Clinic clinic){
        String status = "";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)
                    url.openConnection();
            //Step 2: define the request
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type","application/json");
            //step 3: prepare the post data
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", clinic.name);
            jsonObject.put("rating", clinic.rating);
            jsonObject.put("latitute", clinic.latitute);
            jsonObject.put("longitute", clinic.longitute);
            jsonObject.put("impression", clinic.impression);
            jsonObject.put("lead_physician", clinic.lead_physician);
            jsonObject.put("specialization",clinic.specialization);
            jsonObject.put("average_price",clinic.average_price);
            //step 4: send json to the webservice
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonObject.toString());
            os.flush();
            status = conn.getResponseCode() + ": " + conn.getResponseMessage();
            os.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }
}