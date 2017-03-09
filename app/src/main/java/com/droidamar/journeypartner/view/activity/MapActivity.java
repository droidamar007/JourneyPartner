package com.droidamar.journeypartner.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.droidamar.journeypartner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationSource.OnLocationChangedListener {
    //Distance in meters
    private final long MINIMUM_DISTANCE = 1;

    //Default in milliseconds
    private final long MINIMUM_TIME = 1000 * 60;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initViews();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCurrentLocation();
            }
        });
    }

    private void initViews() {
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.maptest);
        mapFragment.getMapAsync(this);
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );

            Toast.makeText(MapActivity.this, message,
                    Toast.LENGTH_LONG).show();
            initializeMap(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initializeMap(location.getLatitude(), location.getLongitude());
        startLocationFinder();
    }

    @Override
    public void onLocationChanged(Location location) {
        String message = String.format("New Location \n Longitude: %1$s \n Latitude: %2$s",
                location.getLongitude(), location.getLatitude());
        Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG).show();
        initializeMap(location.getLatitude(), location.getLongitude());
    }

    private void initializeMap(double lat, double lang) {
        if (map != null) {

            LatLng position = new LatLng(lat, lang);
            MarkerOptions marker = new MarkerOptions();
            marker.position(position);
            marker.title("Linchpin is situated here!");
            map.addMarker(marker);
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            if (map == null) {
                Toast.makeText(this, "Sorry..", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationFinder() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME, MINIMUM_DISTANCE, (LocationListener) this);

        locationManager.getLastKnownLocation()
    }

    private void getCompleteAddress(double lat, double lang) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(lat, lang, 1);
            // If any additional address line present than only,
            // check with max available address lines by getMaxAddressLineIndex()
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
