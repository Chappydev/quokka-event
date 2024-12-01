package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.Map;

/**
 * An activity to display a facility's details
 */
public class AdminFacilityDetails extends AppCompatActivity {
    TextView facilityName;
    TextView location;
    Button backButton;
    Button deleteButton;
    Map<String, Object> facility_details;

    /**
     * Initializes activity and loads facility list.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_details);
        facilityName = findViewById(R.id.facility_details_name);
        location = findViewById(R.id.facility_details_location);
        backButton = findViewById(R.id.facility_details_back_button);
        deleteButton = findViewById(R.id.delete_facility_button);
        DatabaseManager db = DatabaseManager.getInstance(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            facility_details = (Map<String, Object>) extras.get("facility");
            String facility = facility_details.get("facilityName").toString();
            String locationStr = facility_details.get("facilityLocation").toString();
            facilityName.setText(facility);
            location.setText(locationStr);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Return to facility list when clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Delete the facility from database and return admin to facility list
             * @param view
             */
            @Override
            public void onClick(View view) {
                String facilityId = (String)facility_details.get("facilityId");
                db.deleteFacility(facilityId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        returnToFacilityList();
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            }
        });

    }

    /**
     * Return to admin's Facility List
     */
    void returnToFacilityList(){
        Intent intent = new Intent(this, AdminFacilityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
