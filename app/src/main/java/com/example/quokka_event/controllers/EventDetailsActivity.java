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

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());

        // Views
        TextView eventNameText = findViewById(R.id.event_name_text);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView organizerText = findViewById(R.id.organizer_text);
        TextView statusText = findViewById(R.id.status_text);
        Button cancelButton = findViewById(R.id.button5);

        eventNameText.setText(event.getEventName());
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: "); // event class is missing time
        locationText.setText("Location: " + event.getEventLocation());
        organizerText.setText("Organizer: ");
        statusText.setText(event.isDeadline() ? "Closed" : "Open");

        // Set the click listener for the "Cancel" button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removeFromWaitlist();
                backToEventsList();
            }
        });
    }

    /**
     * Returns to event list page once cancel button is pressed.
     * @author Soaiba
     */
    private void backToEventsList() {
        Intent intent = new Intent(this, MyEventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private Event getEventById(String eventId) {
        return new Event(eventId, "Sample Event", new Date(), new Date(), "Sample Location", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
