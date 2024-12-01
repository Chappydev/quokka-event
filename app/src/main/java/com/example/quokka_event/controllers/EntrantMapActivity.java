package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

/**
 * This activity serves to open a google maps fragment using the
 * Google Maps API and displays the location of a selected entrant
 */
public class EntrantMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String eventId;
    private DatabaseManager db;

    /**
     * Initializes activity, gets event ID from intent and prepares map
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        eventId = getIntent().getStringExtra("eventId");
        db = DatabaseManager.getInstance(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Loads map once user location data is retrieved
     * @author speakerchef
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Get entrants for this event and plot their locations
        db.getWaitlistEntrants(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<Map<String, Object>> entrants = (ArrayList<Map<String, Object>>) result;

                for (Map<String, Object> entrant : entrants) {
                    if (!entrant.isEmpty() && entrant.get("latitude") != null) {
                        Double lat = (Double) entrant.get("latitude");
                        Double lng = (Double) entrant.get("longitude");
                        Log.d("LOCATION", "onSuccess: " + lat + lng);
                        String name = (String) entrant.get("name");

                        if (lat != null && lng != null && name != null) {
                            LatLng position = new LatLng(lat, lng);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title(name));
                        } else {
                            LatLng position = new LatLng(53.5461, -113.4937);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title("Default"));
                        }
                    }
                }

                LatLng center = new LatLng(53.5461, -113.4937);
                if (!entrants.isEmpty()) {
                    Map<String, Object> firstEntrant = entrants.get(0);
                    if (!firstEntrant.isEmpty() && firstEntrant.get("latitude") != null) {
                        Double lat = (Double) firstEntrant.get("latitude");
                        Double lng = (Double) firstEntrant.get("longitude");
                        if (lat != null && lng != null) {
                            center = new LatLng(lat, lng);
                        }
                    }
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 0));
            }

            @Override
            public void onError(Exception exception) {
                Log.e("EntrantMap", "Error loading entrants", exception);
            }
        });
    }

}