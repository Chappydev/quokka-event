package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This activity displays a details of an event.
 * @author Soaiba
 */
public class EventDetailsActivity extends AppCompatActivity {
    private DatabaseManager dbManager;
    private String eventId;
    private User user;
    private String userId;

    @Override
    /**
     * This method displays a info on event based on which event user interacted with on my events page.
     * Handles accept, deny, cancel button activity.
     * @author Soaiba
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_details);
        dbManager = DatabaseManager.getInstance(this);

        user = User.getInstance(this);
        userId = user.getDeviceID();

        // Get data from Intent
        eventId = getIntent().getStringExtra("event_id");
        String eventName = getIntent().getStringExtra("event_name");
        Date eventDate = new Date(getIntent().getLongExtra("event_date", -1));
        String eventLocation = getIntent().getStringExtra("event_location");
        String status = getIntent().getStringExtra("status");

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
        Button backButton = findViewById(R.id.back_button);

        // Display data
        eventNameText.setText(eventName);
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: TBD"); // TODO: Update once time data is available
        locationText.setText("Location: " + eventLocation);
        organizerText.setText("Organizer: TBD"); // TODO: Update once organizer data is available
        statusText.setText("Status: " + status);

        // Set button visibility based on status
        setButtonVisibility(status, acceptButton, denyButton, cancelButton);

        // Set click listeners for the buttons
        acceptButton.setOnClickListener(v -> {
            DatabaseManager.getInstance(this).updateEventStatus(eventId, userId, "Accepted", new DbCallback() {
                @Override
                /**
                 * This method updates status on the database and navigates user to confirmation screen.
                 * @author Soaiba
                 */
                public void onSuccess(Object response) {
                    goToConfirm("Accept", eventName);
                }

                @Override
                /**
                 * This method helps debug failiure to update database
                 * @author Soaiba
                 */
                public void onError(Exception e) {
                    Log.e("EventDetailsActivity", "Failed to update status to accepted", e);
                }
            });
        });

        denyButton.setOnClickListener(v -> {
            DatabaseManager.getInstance(this).updateEventStatus(eventId, userId, "Declined", new DbCallback() {
                @Override
                public void onSuccess(Object response) {
                    goToConfirm("Deny", eventName);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("EventDetailsActivity", "Failed to update status to declined", e);
                }
            });
        });

        cancelButton.setOnClickListener(v -> {
            DatabaseManager.getInstance(this).updateEventStatus(eventId, userId, "Unjoined", new DbCallback() {
                @Override
                public void onSuccess(Object response) {
                    goToConfirm("Cancel", eventName);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("EventDetailsActivity", "Failed to update status to unjoined", e);
                }
            });
        });

        backButton.setOnClickListener(v -> goToMyEventsPage());
    }

    /**
     * This method sets button visibility based on status.
     * @author Soaiba
     * @param status enrollment status.
     * @param acceptButton accept button.
     * @param denyButton deny button.
     * @param cancelButton back button.
     */
    private void setButtonVisibility(String status, Button acceptButton, Button denyButton, Button cancelButton) {
        if ("Waiting".equalsIgnoreCase(status)) {
            acceptButton.setEnabled(false);
            denyButton.setEnabled(false);
            cancelButton.setEnabled(true);
        } else if ("Invited".equalsIgnoreCase(status)) {
            acceptButton.setEnabled(true);
            denyButton.setEnabled(true);
            cancelButton.setEnabled(false);
        } else if ("Accepted".equalsIgnoreCase(status) || "Declined".equalsIgnoreCase(status) || "Unjoined".equalsIgnoreCase(status) || "Not Invited".equalsIgnoreCase(status)) {
            acceptButton.setEnabled(false);
            denyButton.setEnabled(false);
            cancelButton.setEnabled(false);
        } else {
            acceptButton.setEnabled(true);
            denyButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    /**
     * This method navigates to the confirmation activity with a message type and event name.
     * @author Soaiba
     * @param eventName name of event user is interacting with.
     * @param messageType which button they are interacting with.
     */
    private void goToConfirm(String messageType, String eventName) {
        Intent intent = new Intent(EventDetailsActivity.this, ConfirmationActivity.class);
        intent.putExtra("message_type", messageType);
        intent.putExtra("event_name", eventName);
        startActivity(intent);
    }

    private void goToMyEventsPage() {
        Intent intent = new Intent(EventDetailsActivity.this, MyEventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
