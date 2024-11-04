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
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_details);

        // Get the event by eventID
        String eventId = getIntent().getStringExtra("event_id");
        Event event = getEventById(eventId);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());

        // Views
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

        // Display info
        eventNameText.setText(event.getEventName());
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: "); // TODO event class is missing time
        locationText.setText("Location: " + event.getEventLocation());
        organizerText.setText("Organizer: ");
        statusText.setText(event.isDeadline() ? "Closed" : "Open");

        // Set the click listener for the accept button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConfirm("Accept", event.getEventName());
            }
        });

        // Set the click listener for the deny button
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConfirm("Deny", event.getEventName());
            }
        });

        // Set the click listener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConfirm("Cancel", event.getEventName());
            }
        });
    }

    private Event getEventById(String eventId) {
        return new Event(eventId, "Sample Event", new Date(), new Date(), "Sample Location", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private void goToConfirm(String messageType, String eventName) {
        Intent intent = new Intent(EventDetailsActivity.this, ConfirmationActivity.class);
        intent.putExtra("message_type", messageType);
        intent.putExtra("event_name", eventName);
        startActivity(intent);
    }
}
