package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quokka_event.models.event.Event;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details); // Replace with your actual layout file name if different

        // Retrieve the event ID passed through the intent
        String eventId = getIntent().getStringExtra("event_id");

        // Fetch the event details based on the eventId
        // Here, you would normally query your data source to retrieve the event details.
        // For simplicity, I'll demonstrate using a sample event.
        Event event = getEventById(eventId);

        // Initialize the views
        TextView eventNameText = findViewById(R.id.event_name_text);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView organizerText = findViewById(R.id.organizer_text);
        TextView statusText = findViewById(R.id.status_text);

        // Set the event details
        eventNameText.setText(event.getEventName());
        dateText.setText(event.getEventDate().toString()); // Format as needed
        timeText.setText("N/A"); // Add time if applicable
        locationText.setText(event.getEventLocation());
        organizerText.setText("Organizer Info Here"); // Add organizer info if applicable
        statusText.setText(event.isDeadline() ? "Closed" : "Open");
    }

    // Dummy method to simulate fetching event details by ID
    private Event getEventById(String eventId) {
        // In a real application, you would fetch the event from a database or data source
        return new Event(eventId, "Sample Event", new Date(), new Date(), "Sample Location", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
