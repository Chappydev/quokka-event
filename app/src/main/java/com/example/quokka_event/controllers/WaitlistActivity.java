package com.example.quokka_event.controllers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.entrant.EventManager;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.views.Toolbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * This activity displays information about events for users to waitlist for.
 * @author Simon
 */
public class WaitlistActivity extends AppCompatActivity {
    private DatabaseManager db;
    private Event event;
    private String eventId;
    private TextView eventTitle;
    private TextView dateText;
    private TextView timeText;
    private TextView locationText;
    private TextView organizerText;
    private EventManager eventManager;
    private Button joinButton;
    private Button exitButton;
    private ImageView posterView;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    /**
     * This method displays event details.
     * Handles join button activity.
     *
     * @author Simon, Soaiba, Daniel
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_waitlist_details);

        User currentUser = User.getInstance(this.getApplicationContext());
        db = DatabaseManager.getInstance(this);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Toolbar.initializeToolbar(findViewById(R.id.toolbar2), this);

        Bundle extras = getIntent().getExtras();
        Log.d("DB", "onSuccess: " + extras.get("facilityName"));

        if (extras != null) {
            Log.d("DB", "onSuccess: " + extras);
            event = new Event();
            event.setEventDate((Date) extras.get("eventDate"));
            event.setEventLocation((String) extras.get("eventLocation"));
            event.setEventID((String) extras.getString("eventId"));
            event.setEventName((String) extras.get("eventName"));
            event.setMaxParticipants((int) extras.get("maxParticipants"));
            event.setRegistrationDeadline((Date) extras.get("registrationDeadline"));
            event.setMaxWaitlist((int) extras.get("maxWaitlist"));
            event.setPosterImage((String) extras.get("posterImagePath"));
            event.setGeolocationEnabled(getIntent().getBooleanExtra("geolocationEnabled", false));
            event.setOrganizerName((String) extras.get("facilityName"));
        }

        // Initialize views and buttons
        eventTitle = findViewById(R.id.event_name_text);
        dateText = findViewById(R.id.date_text);
        timeText = findViewById(R.id.time_text);
        locationText = findViewById(R.id.location_text);
        organizerText = findViewById(R.id.organizer_text);
        joinButton = findViewById(R.id.join_waitlist_button);
        posterView = findViewById(R.id.imageView);

        eventId = event.getEventID();
        Log.d("EventDetailsActivity", "Event Id: " + eventId);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(event.getEventDate());

        // Display info
        eventTitle.setText(event.getEventName());
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: " + formattedTime);
        locationText.setText("Location: " + event.getEventLocation());
        if (event.getOrganizerName() != null) {
            organizerText.setText("Organizer: " + event.getOrganizerName());
        } else {
            organizerText.setText("Organizer: null");
        }

        // display the image if there is one
        String posterPath = event.getPosterImage();

        Log.d("EventDetailsActivity", "Event Id: " + eventId);
        Log.d("EventDetailsActivity", "Poster Path: " + posterPath);
        if (posterPath != null && !posterPath.isEmpty()) {
            fetchAndApplyImage(eventId, posterView);
        } else {
            posterView.setVisibility(View.GONE); // Hide ImageView if no poster exists
        }

        // Join button activity
        joinButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When join button is clicked add the user to the event's waitlist and route back to
             * MyEventsActivity
             * @author Chappydev, Soaiba
             * @param view
             */
            @Override
            public void onClick(View view) {
                if (event.getGeolocationEnabled()){
                    new AlertDialog.Builder(WaitlistActivity.this)
                            .setTitle("Join an event with Geolocation.")
                            .setMessage("This event has geolocation enabled. Would you still like to join?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                String userId = User.getInstance(WaitlistActivity.this).getDeviceID();
                                db.getEnrolls(event.getEventID(), new DbCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) result;
                                        for (Map<String,Object> instance : data){
                                            String user = (String) instance.get("deviceId");
                                            Log.d("geg", "user: " + user);
                                            Log.d("geg", "userID: " + userId);
                                            Log.d("geg", "Instance: " + instance);
                                            if (Objects.equals(user, userId)){
                                                Toast.makeText(WaitlistActivity.this, "You are already part of this event!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                        db.joinWaitlist(event.getEventID(), userId, new DbCallback() {
                                            @Override
                                            public void onSuccess(Object result) {
                                                Toast.makeText(WaitlistActivity.this, "You joined the waitlist for '" + event.getEventName() + "'", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(WaitlistActivity.this, ConfirmationActivity.class);
                                                intent.putExtra("message_type", "JoinWaitlist");
                                                intent.putExtra("event_name", event.getEventName());

                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onError(Exception exception) {
                                                Log.e("DB", "WaitlistActivity onError: ", exception);
                                                Toast.makeText(WaitlistActivity.this, "Something went wrong adding you to the waitlist", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        Toast.makeText(WaitlistActivity.this, "An unknown error occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            })
                            .show();

                } else {
                    String userId = User.getInstance(WaitlistActivity.this).getDeviceID();
                    db.getEnrolls(event.getEventID(), new DbCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) result;
                            for (Map<String,Object> instance : data){
                                String user = (String) instance.get("deviceId");
                                if (user == userId){
                                    Toast.makeText(WaitlistActivity.this, "You are already part of this event!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            db.joinWaitlist(event.getEventID(), userId, new DbCallback() {
                                @Override
                                public void onSuccess(Object result) {
                                    Toast.makeText(WaitlistActivity.this, "You joined the waitlist for '" + event.getEventName() + "'", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(WaitlistActivity.this, ConfirmationActivity.class);
                                    intent.putExtra("message_type", "JoinWaitlist");
                                    intent.putExtra("event_name", event.getEventName());

                                    startActivity(intent);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Log.e("DB", "WaitlistActivity onError: ", exception);
                                    Toast.makeText(WaitlistActivity.this, "Something went wrong adding you to the waitlist", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            Toast.makeText(WaitlistActivity.this, "An unknown error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }


    /**
     * Fetches and displays an image using Glide for display.
     *
     * @param eventId   Id of an event
     * @param imageView Where the image will be displayed
     * @author mylayambao
     */
    private void fetchAndApplyImage(String eventId, ImageView imageView) {
        StorageReference posterRef = storageRef.child("Events/" + eventId + ".jpg");

        posterRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("FetchImage", "Loading image from URI: " + uri.toString());

                    Glide.with(WaitlistActivity.this)
                            .load(uri)
                            .into(imageView);
                })
                .addOnFailureListener(e -> {
                    Log.e("FetchImage", "Failed to load image for event: " + eventId, e);
                    Toast.makeText(WaitlistActivity.this,
                            "Unable to load poster image",
                            Toast.LENGTH_SHORT).show();
                });
    }
}
