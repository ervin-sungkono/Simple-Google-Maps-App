package com.example.gslclocationbasedservice;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gslclocationbasedservice.database.DBHelper;
import com.example.gslclocationbasedservice.database.MarkerDB;
import com.example.gslclocationbasedservice.model.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView searchView;
    MarkerDB markerDB;
    Button addMarkerButton, showMarkerButton, removeMarkerButton, hideMarkerButton;

    private LatLng currentPinpoint;
    private Marker activeMarker;

    private List<Position> positionsList = new ArrayList<>();

    private Location lastKnownLocation;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        markerDB = new MarkerDB(this);

        addMarkerButton = findViewById(R.id.add_marker);
        showMarkerButton = findViewById(R.id.show_marker);
        removeMarkerButton = findViewById(R.id.remove_marker);
        hideMarkerButton = findViewById(R.id.hide_marker);

        removeMarkerButton.setVisibility(View.INVISIBLE);
        hideMarkerButton.setVisibility(View.INVISIBLE);

        searchView = findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(addressList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    }else{
                        Address address = addressList.get(0);

                        currentPinpoint = new LatLng(address.getLatitude(), address.getLongitude());
                        activeMarker = mMap.addMarker(new MarkerOptions().position(currentPinpoint).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPinpoint, 10));
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        activeMarker = mMap.addMarker(
                new MarkerOptions().
                        position(defaultLocation).
                        title(defaultLocation.latitude + " : " + defaultLocation.longitude)
        );
        currentPinpoint = defaultLocation;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));

        addMarkerButton.setOnClickListener(v->{
            markerDB.insertMarker(currentPinpoint);
            Toast.makeText(this, "Pinpoint added!", Toast.LENGTH_SHORT).show();
            addMarkerButton.setVisibility(View.INVISIBLE);
            removeMarkerButton.setVisibility(View.VISIBLE);
        });

        removeMarkerButton.setOnClickListener(v->{
            if(activeMarker == null) return;
            String id = activeMarker.getSnippet();
            markerDB.deleteMarker(id);
            activeMarker.remove();
            Toast.makeText(this, "Pinpoint removed!", Toast.LENGTH_SHORT).show();
            addMarkerButton.setVisibility(View.VISIBLE);
            removeMarkerButton.setVisibility(View.INVISIBLE);
        });

        showMarkerButton.setOnClickListener(v->{
            positionsList = markerDB.getAllMarkers();
            for(int i = 0; i < positionsList.size(); i++){
                Position p = positionsList.get(i);
                LatLng point = p.getPoint();
                mMap.addMarker(
                        new MarkerOptions().
                                position(point).
                                title(point.latitude + " : " + point.longitude).
                                snippet(p.getId() + "")
                );
            }
            Toast.makeText(this, "All pinpoints are shown in map", Toast.LENGTH_SHORT).show();
            showMarkerButton.setVisibility(View.INVISIBLE);
            hideMarkerButton.setVisibility(View.VISIBLE);
        });

        hideMarkerButton.setOnClickListener(v->{
            mMap.clear();
            activeMarker = mMap.addMarker(
                    new MarkerOptions().
                            position(currentPinpoint).
                            title(currentPinpoint.latitude + " : " + currentPinpoint.longitude)
            );
            Toast.makeText(this, "Pinpoints hidden", Toast.LENGTH_SHORT).show();
            showMarkerButton.setVisibility(View.VISIBLE);
            hideMarkerButton.setVisibility(View.INVISIBLE);
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                activeMarker.remove();
                activeMarker = mMap.addMarker(
                        new MarkerOptions().
                                position(point).
                                title(point.latitude + " : " + point.longitude)
                );
                currentPinpoint = point;
                addMarkerButton.setVisibility(View.VISIBLE);
                removeMarkerButton.setVisibility(View.INVISIBLE);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                activeMarker = marker;
                addMarkerButton.setVisibility(View.INVISIBLE);
                removeMarkerButton.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }
}