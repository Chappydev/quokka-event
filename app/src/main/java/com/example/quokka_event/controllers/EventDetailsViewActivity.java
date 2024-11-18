package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EventDetailsViewActivity extends AppCompatActivity {
    private TextView eventTitle;
    private TextView eventDateLabel;
    private TextView eventTimeLabel;
    private TextView eventLocationLabel;
    private TextView eventCapacityLabel;
    private TextView eventWaitlistLabel;
    private TextView eventDeadlineLabel;
    private TextView eventDescriptionLabel;
    private Button backButton;
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_view);

        eventTitle = findViewById(R.id.event_title);
        eventDateLabel = findViewById(R.id.event_date_label);
        eventTimeLabel = findViewById(R.id.event_time_label);
        eventLocationLabel = findViewById(R.id.event_location_label);
        eventCapacityLabel = findViewById(R.id.event_capacity_label);
        eventWaitlistLabel = findViewById(R.id.event_waitlist_label);
        eventDeadlineLabel = findViewById(R.id.event_deadline_label);
        eventDescriptionLabel = findViewById(R.id.event_description_label);
        backButton = findViewById(R.id.back_button_bottom);

        db = DatabaseManager.getInstance(this);

        // Get event ID from intent
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());
    }


    /**
     * Loads the event details for the selected event
     * @author speakerchef
     * @param eventId
     */
    private void loadEventDetails(String eventId) {
        db.getSingleEvent(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> event = (Map<String, Object>) result;

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    Timestamp eventTimestamp = (Timestamp) event.get("eventDate");
                    Timestamp deadlineTimestamp = (Timestamp) event.get("registrationDeadline");
                    Date eventDate = eventTimestamp.toDate();
                    Date deadline = deadlineTimestamp.toDate();

                    // set all the event fields
                    eventTitle.setText((String) event.get("eventName"));
                    eventDateLabel.setText("Date: " + dateFormat.format(eventDate));
                    eventTimeLabel.setText("Time: " + timeFormat.format(eventDate));
                    eventLocationLabel.setText("Location: " + event.get("eventLocation"));
                    long maxCapacity = (long) event.get("maxParticipants");
                    long maxWaitlist = (long) event.get("maxParticipants");

                    // clean display if capacity is maxed
                    String capacityText = (maxCapacity != Integer.MAX_VALUE ? String.valueOf(maxCapacity) : "Unlimited");
                    String waitlistText = (maxWaitlist != Integer.MAX_VALUE ? String.valueOf(maxWaitlist) : "Unlimited");

                    eventCapacityLabel.setText("Capacity: " + capacityText + " participants");
                    eventWaitlistLabel.setText("Waitlist Capacity: " + waitlistText + " spots");
                    eventDeadlineLabel.setText("Registration Deadline: " + dateFormat.format(deadline));

                    // description and possible nulls
                    String description = (String) event.get("description");
                    eventDescriptionLabel.setText(description != null ? description : "No description available");

                } catch (Exception e) {
                    Log.e("EventDetails", "Error formatting event data", e);
                    Toast.makeText(EventDetailsViewActivity.this,
                            "Error displaying event details",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("EventDetailsView", "Error loading event", e);
                Toast.makeText(EventDetailsViewActivity.this,
                        "Error loading event details",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}