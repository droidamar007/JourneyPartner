package com.droidamar.journeypartner.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.droidamar.journeypartner.R;
import com.droidamar.journeypartner.tasks.GeoCoderTask;
import com.droidamar.journeypartner.utils.Validator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        View.OnClickListener, TextView.OnEditorActionListener, GeoCoderTask.Task {

    private final String TAG = getClass().getName();

    private static final int LOCATION_REQUEST_CODE = 1001;
    //Distance in meters
    private final long MINIMUM_DISTANCE = 1;

    //Default in milliseconds
    private final long MINIMUM_TIME = 1000 * 60;

    private LocationManager locationManager;

    private GoogleMap googleMap;

    private FloatingActionButton fab;

    private Location location;

    private EditText etSearchLocation;
    private ImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();

        ivSearch.setOnClickListener(this);
        etSearchLocation.setOnEditorActionListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCompleteAddress(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });
    }

    private void initViews() {
        etSearchLocation = (EditText) findViewById(R.id.etSearchLocation);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        showCurrentLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        String message = String.format("New Location \n Longitude: %1$s \n Latitude: %2$s",
                location.getLongitude(), location.getLatitude());
        Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG).show();

        final float distanceRemain = this.location.distanceTo(location);
        if (distanceRemain <= 1) {
            //TODO start alarm
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        String providerStatus = "";
        switch (i) {
            case LocationProvider.AVAILABLE:
                providerStatus = "available";
                break;
            case LocationProvider.OUT_OF_SERVICE:
                providerStatus = "out of service";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                providerStatus = "currently unavailable";
                break;
        }

        Toast.makeText(this, s + " is " + providerStatus + " now.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, s + " is enabled now.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, s + " is unavailable now.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    //TODO Requested permission denied
                }
                break;
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.ivSearch:
                final GeoCoderTask geoCoderTask = new GeoCoderTask(this, googleMap);
                geoCoderTask.setOnTaskDone(this);
                geoCoderTask.execute(etSearchLocation.getText().toString());
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEARCH:
                //TODO Same code as ivSearch click action
                break;
        }
        return false;
    }

    private void showCurrentLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME,
                MINIMUM_DISTANCE, this);

        final List<String> providers = locationManager.getProviders(true);

        if (!Validator.isListNullOrEmpty(providers)) {
            for (String provider : providers) {
                if (Validator.isStringNullOrEmpty(provider)) {
                    continue;
                }
                final Location location = locationManager.getLastKnownLocation(provider);
                if (null == location) {
                    continue;
                }
                if (null == this.location || location.getAccuracy() < this.location.getAccuracy()) {
                    this.location = location;
                }
            }
        }
        if (null != location) {
            moveMapToLocation(new LatLng(location.getLatitude(), location.getLongitude()), "");
        } else {
            Log.i(TAG, "Unable to fetch device location");
        }
    }

    private void requestLocationPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    private void moveMapToLocation(final LatLng latlng, final String address) {
        if (null != googleMap && null != latlng) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(latlng);

            if (!Validator.isStringNullOrEmpty(address))
                marker.title(address);
            else
                marker.title(getCompleteAddress(latlng));

            googleMap.addMarker(marker);
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }
    }

    private String getCompleteAddress(final LatLng latlng) {

        try {
            final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            // Here 1 ($[1-5]) represent max location result to returned
            final List<Address> addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            if (!Validator.isListNullOrEmpty(addresses)) {
                // If any additional address line present than only,
                // check with max available address lines by getMaxAddressLineIndex()
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();

                System.out.println("Complete address: " + address + " " + city + " " + state + " "
                        + country + " " + postalCode);
                return address + " " + city + " " + state + " "
                        + country + " " + postalCode;
            } else {
                Snackbar.make(fab, "Unable to fetch your complete address.", 1000);
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onGeoCoderTaskSucceeded(LatLng latLng, String address) {
        System.out.println("Find address is: " + address);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        moveMapToLocation(latLng, address);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
    }
}
