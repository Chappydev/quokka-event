package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_details);

        // Retrieve data from the Intent
        String eventId = getIntent().getStringExtra("event_id");
        String eventName = getIntent().getStringExtra("event_name");
        long eventDateMillis = getIntent().getLongExtra("event_date", -1);
        Date eventDate = new Date(eventDateMillis);
        String eventLocation = getIntent().getStringExtra("event_location");
        boolean isDeadlinePassed = getIntent().getBooleanExtra("event_deadline_passed", false);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(eventDate);

        // Retrieve views
        TextView eventNameText = findViewById(R.id.event_name_text);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView organizerText = findViewById(R.id.organizer_text);
        TextView statusText = findViewById(R.id.status_text);

        // Buttons
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
     */
    private void goToConfirm(String messageType, String eventName) {
        Intent intent = new Intent(EventDetailsActivity.this, ConfirmationActivity.class);
        intent.putExtra("message_type", messageType);
        intent.putExtra("event_name", eventName);
        startActivity(intent);
    }
}
