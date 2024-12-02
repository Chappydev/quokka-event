package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.event.LotteryChecker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private String eventName;
    private long maxSlots;
    private User user;
    private String userId;
    private ImageView posterView;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    /**
     * This method displays a info on event based on which event user interacted with on my events page.
     * Handles accept, deny, cancel, back button activity.
     * @author Soaiba
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_details);

        dbManager = DatabaseManager.getInstance(this);

        // Get user
        user = User.getInstance(this);
        userId = user.getDeviceID();

        // Get data from Intent
        eventId = getIntent().getStringExtra("event_id");
        eventName = getIntent().getStringExtra("event_name");
        maxSlots = getIntent().getLongExtra("maxParticipants", 0);
        String eventName = getIntent().getStringExtra("event_name");
        Date eventDate = new Date(getIntent().getLongExtra("event_date", -1));
        String eventLocation = getIntent().getStringExtra("event_location");
        String status = getIntent().getStringExtra("status");

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(eventDate);

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(eventDate);

        // Initialize Views
        TextView eventNameText = findViewById(R.id.event_name_text);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView organizerText = findViewById(R.id.organizer_text);
        TextView statusText = findViewById(R.id.status_text);
        posterView = findViewById(R.id.imageView);

        // Initialize buttons
        Button acceptButton = findViewById(R.id.accept_button);
        Button denyButton = findViewById(R.id.deny_button);
        Button leaveButton = findViewById(R.id.leave_button);
        Button backButton = findViewById(R.id.back_button);

        // Display data
        eventNameText.setText(eventName);
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: " + formattedTime);
        locationText.setText("Location: " + eventLocation);
        organizerText.setText("Organizer: TBD"); // TODO: Update once organizer data is available
        statusText.setText("Status: " + status);

        // Set button visibility based on status
        setButtonVisibility(status, acceptButton, denyButton, leaveButton);

        // Set click listeners for the buttons
        acceptButton.setOnClickListener(v -> {
            DatabaseManager.getInstance(this).updateEventStatus(eventId, userId, "Accepted", new DbCallback() {
                @Override
                /**
                 * This method updates status on the database and navigates user to confirmation screen.
                 * @author Soaiba
                 * @param response
                 */
                public void onSuccess(Object response) {
                    Toast.makeText(EventDetailsActivity.this, "You have accepted the invitation to '" + eventName + "'", Toast.LENGTH_SHORT).show();
                    goToConfirm("Accept", eventName);
                }
                @Override
                /**
                 * This method logs failure to update database.
                 * @author Soaiba
                 * @param e
                 */
                public void onError(Exception e) {
                    Log.e("EventDetailsActivity", "Failed to update status to accepted", e);
                    Toast.makeText(EventDetailsActivity.this, "You couldn't accept the invitation to '" + eventName + "'", Toast.LENGTH_SHORT).show();
                }
            });
        });

        denyButton.setOnClickListener(v -> {
            DatabaseManager.getInstance(this).updateEventStatus(eventId, userId, "Declined", new DbCallback() {
                @Override
                /**
                 * This method updates status on the database and navigates user to confirmation screen.
                 * @author Soaiba
                 * @param response
                 */
                public void onSuccess(Object response) {
                    Toast.makeText(EventDetailsActivity.this, "You have denied the invitation to '" + eventName + "'", Toast.LENGTH_SHORT).show();
                    goToConfirm("Deny", eventName);
                    runLottery();

                }

                @Override
                /**
                 * This method logs failure to update database.
                 * @author Soaiba
                 * @param e
                 */
                public void onError(Exception e) {
                    Log.e("EventDetailsActivity", "Failed to update status to declined", e);
                    Toast.makeText(EventDetailsActivity.this, "You couldn't deny the invitation to '" + eventName + "'", Toast.LENGTH_SHORT).show();
                }
            });
        });

        leaveButton.setOnClickListener(v -> {
            DatabaseManager.getInstance(this).updateEventStatus(eventId, userId, "Unjoined", new DbCallback() {
                @Override
                /**
                 * This method updates status on the database and navigates user to confirmation screen.
                 * @author Soaiba
                 * @param response
                 */
                public void onSuccess(Object response) {
                    Toast.makeText(EventDetailsActivity.this, "You have canceled your waitlist spot in '" + eventName + "'", Toast.LENGTH_SHORT).show();
                    goToConfirm("Leave", eventName);
                    removeEvent();
                }

                @Override
                /**
                 * This method logs failure to update database.
                 * @author Soaiba
                 * @param e
                 */
                public void onError(Exception e) {
                    Log.e("EventDetailsActivity", "Failed to update status to unjoined", e);
                    Toast.makeText(EventDetailsActivity.this, "You couldn't cancel your waitlist spot in '" + eventName + "'", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Back button activity
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
        } else if ("Accepted".equalsIgnoreCase(status) || "Declined".equalsIgnoreCase(status) || "Not Invited".equalsIgnoreCase(status)) {
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

    /**
     * This method removes an unjoined event from the page.
     * @author Soaiba
     */
    private void removeEvent() {
        Intent intent = new Intent(this, MyEventsActivity.class);
        intent.putExtra("remove_event_id", eventId);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * This method navigates to the user's events page.
     * @author Soaiba
     */
    private void goToMyEventsPage() {
        Intent intent = new Intent(EventDetailsActivity.this, MyEventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches and displays an image using Glide for display.
     * @author mylayambao
     * @param eventId Id of an event
     * @param imageView Where the image will be displayed
     */
    private void fetchAndApplyImage(String eventId, ImageView imageView) {
        StorageReference posterRef = storageRef.child("Events/" + eventId + ".jpg");

        posterRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d("FetchImage", "Loading image from URI: " + uri.toString());

                    Glide.with(EventDetailsActivity.this)
                            .load(uri)
                            .into(imageView);
                })
                .addOnFailureListener(e -> {
                    Log.e("FetchImage", "Failed to load image for event: " + eventId, e);
                    Toast.makeText(EventDetailsActivity.this,
                            "Unable to load poster image",
                            Toast.LENGTH_SHORT).show();
                });
    }

    void runLottery() {
        Log.d("Lottery", "runLottery: running" + eventId + " " + eventName + " " + maxSlots);
        Intent intent = new Intent(this, LotteryChecker.class);
        Log.d("Lottery", "runLottery: running" + eventId + " " + eventName + " " + maxSlots);
        intent.putExtra("eventId", eventId);
        intent.putExtra("eventName", eventName);
        intent.putExtra("maxParticipants", maxSlots);
        intent.putExtra("lotteryType", "replacement");
        LotteryChecker lotteryChecker = new LotteryChecker();
        lotteryChecker.onReceive(this, intent);
    }
}
