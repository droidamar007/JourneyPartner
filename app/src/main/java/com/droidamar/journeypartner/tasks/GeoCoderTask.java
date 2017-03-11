package com.droidamar.journeypartner.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.droidamar.journeypartner.utils.Validator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by Amar
 */

// An AsyncTask class for accessing the GeoCoding Web Service
public class GeoCoderTask extends AsyncTask<String, Void, List<Address>> {

    private final Context context;
    private final GoogleMap googleMap;
    private Task task;

    public GeoCoderTask(final Context context, final GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
    }

    public void setOnTaskDone(Task task) {
        this.task = task;
    }

    @Override
    protected List<Address> doInBackground(String... locationName) {
        // Creating an instance of GeoCoder class
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            // Getting a maximum of 3 Address that matches the input text
            addresses = geocoder.getFromLocationName(locationName[0], 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {

        if (Validator.isListNullOrEmpty(addresses)) {
            Toast.makeText(context, "No Location found", Toast.LENGTH_SHORT).show();
        } else {

            // Clears all the existing markers on the map
            googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for (int i = 0; i < addresses.size(); i++) {
                final Address address = addresses.get(i);

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                // Creating an instance of GeoPoint, to display in Google Map
                final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                task.onGeoCoderTaskSucceeded(latLng, addressText);
            }
        }
    }

    public interface Task {
        void onGeoCoderTaskSucceeded(final LatLng latLng, final String address);
    }
}

