package com.example.quokka_event;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.admin.ProfileSystem;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.organizer.Facility;

import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DatabaseManager db;
    private static final String TAG = "DB";
    private String lastCreatedEventId;
    private String lastCreatedFacilityId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_landing_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = DatabaseManager.getInstance(this);
        User user = User.getInstance(this);
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.getDeviceUser(new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                user.initialize(
                        (String) map.get("deviceID"),
                        (ProfileSystem) map.get("profile"),
                        (Boolean) map.get("organizer"),
                        (Boolean) map.get("admin")
                );
                Log.d("DB", "onCreate: " + user.getDeviceID());
            }
            @Override
            public void onError(Exception exception) {
                Log.e("DB", "onError: ", exception);
            }
        }, deviceId);

        // button actions
        Button createButton = new Button(this);
        createButton.setText("Test Create Event");
        createButton.setOnClickListener(v -> testCreateEvent());

        Button deleteButton = new Button(this);
        deleteButton.setText("Test Delete Event");
        deleteButton.setOnClickListener(v -> {
            if (lastCreatedEventId != null) {
                testDeleteEvent(lastCreatedEventId);
            } else {
                Toast.makeText(this, "Create an event first", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteProfileButton = new Button(this);
        deleteProfileButton.setText("Test delete Profile");
        deleteProfileButton.setOnClickListener(v -> testDeleteProfile());

        Button addFacilityButton = new Button(this);
        addFacilityButton.setText("Add Facility");
        addFacilityButton.setOnClickListener(v -> {
            testCreateFacility();
        });

        Button deleteFacilityButton = new Button(this);
        deleteFacilityButton.setText("Delete Facility");
        deleteFacilityButton.setOnClickListener(v -> {
            if (lastCreatedFacilityId != null) {
                testDeleteFacility(lastCreatedFacilityId);
            } else {
                Log.e(TAG, "Create a facility first.");
            }
        });

//         test buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.addView(createButton);
        buttonLayout.addView(deleteButton);
        buttonLayout.addView(deleteProfileButton);
        buttonLayout.addView(addFacilityButton);
        buttonLayout.addView(deleteFacilityButton);

        ViewGroup container = findViewById(R.id.landing_page);
        if (container != null) {
            container.addView(buttonLayout);
        }
    }

    private void testCreateEvent() {
        try {
            Event testEvent = new Event();
            testEvent.setEventName("Test Event");
            testEvent.setEventDate(new Date());
            testEvent.setEventLocation("Test Location");
            testEvent.setRegistrationDeadline(new Date(System.currentTimeMillis() + 86400000));
            testEvent.setMaxParticipants(50);
            testEvent.setMaxWaitlist(10);

            Log.d(TAG, "creating event: " + testEvent.getEventName());

            db.addEvent(testEvent, new DbCallback() {
                @Override
                public void onSuccess(Object result) {
                    String eventId = (String) result;
                    lastCreatedEventId = eventId;
                    Log.d(TAG, "Event created successfully with ID: " + eventId);
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(TAG, "Error creating event: ", exception);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up test event: ", e);
        }
    }

    private void testDeleteEvent(String eventId) {
        Log.d(TAG, "deleting event: " + eventId);

        db.deleteEvent(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d(TAG, "Event deleted successfully: " + eventId);
                lastCreatedEventId = null;
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, "Error deleting event: ", exception);
                Toast.makeText(MainActivity.this,
                        "Error deleting event: " + exception.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void testDeleteProfile() {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.deleteProfile(deviceId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d(TAG, "Profile deleted successfully: " + deviceId);
            }

            @Override
            public void onError(Exception exception) {
                Log.d(TAG, "Error deleting profile: " + exception.getMessage());
            }
        });
    }

    private void testCreateFacility(){
        Facility facility = new Facility();
        facility.setFacilityId("");
        facility.setFacilityName("Test Facility");
        facility.setFacilityLocation("Test Location");

        db.addFacility(facility, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d(TAG, "Facility created with ID: " + result);
                lastCreatedFacilityId = result.toString();
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, "Error creating facility: " + exception.getMessage());
            }
        });
    }

    private void testDeleteFacility(String facilityId){
        db.deleteFacility(facilityId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d(TAG, "Facility deleted successfully. ID: " + lastCreatedFacilityId);
                lastCreatedFacilityId = null;
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, "Error deleting Facility with ID: " + lastCreatedFacilityId);
            }
        });
    }
}