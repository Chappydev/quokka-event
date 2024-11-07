package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This activity displays a details of an event.
 * @author Soaiba
 */
public class EventDetailsActivity extends AppCompatActivity {
    @Override
    /**
     * This method displays a info on event based on which event user interacted with on my events page.
     * Handles accept, deny, cancel button activity.
     * @author Soaiba
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_details);

        // Get data from Intent
        String eventId = getIntent().getStringExtra("event_id");
        String eventName = getIntent().getStringExtra("event_name");
        long eventDateMillis = getIntent().getLongExtra("event_date", -1);
        Date eventDate = new Date(eventDateMillis);
        String eventLocation = getIntent().getStringExtra("event_location");
        boolean isDeadlinePassed = getIntent().getBooleanExtra("event_deadline_passed", false);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(eventDate);

        // Initialize Views
        TextView eventNameText = findViewById(R.id.event_name_text);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView organizerText = findViewById(R.id.organizer_text);
        TextView statusText = findViewById(R.id.status_text);

        // Initialize buttons
        Button acceptButton = findViewById(R.id.accept_button);
        Button denyButton = findViewById(R.id.deny_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Display data
        eventNameText.setText(eventName);
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: TBD"); // TODO: Update once time data is available
        locationText.setText("Location: " + eventLocation);
        organizerText.setText("Organizer: TBD"); // TODO: Update once organizer data is available
        statusText.setText(isDeadlinePassed ? "Closed" : "Open");

        // Set click listeners for the buttons
        acceptButton.setOnClickListener(v -> goToConfirm("Accept", eventName));
        denyButton.setOnClickListener(v -> goToConfirm("Deny", eventName));
        cancelButton.setOnClickListener(v -> goToConfirm("Cancel", eventName));
    }

    /**
     * Navigate to the confirmation activity with a message type and event name.
     * @author Soaiba
     * @param eventName name of event user is interacting with
     * @param messageType which button they are interacting with
     */
    private void goToConfirm(String messageType, String eventName) {
        Intent intent = new Intent(EventDetailsActivity.this, ConfirmationActivity.class);
        intent.putExtra("message_type", messageType);
        intent.putExtra("event_name", eventName);
        startActivity(intent);
    }
}
