package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.R;

import java.util.ArrayList;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_details);
        String eventId = getIntent().getStringExtra("event_id");
        Event event = getEventById(eventId);

        TextView eventNameText = findViewById(R.id.event_name_text);
        TextView dateText = findViewById(R.id.date_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView organizerText = findViewById(R.id.organizer_text);
        TextView statusText = findViewById(R.id.status_text);

        eventNameText.setText(event.getEventName());
        dateText.setText(event.getEventDate().toString());
        timeText.setText("N/A");
        locationText.setText(event.getEventLocation());
        organizerText.setText("Organizer Info Here");
        statusText.setText(event.isDeadline() ? "Closed" : "Open");
    }

    private Event getEventById(String eventId) {
        return new Event(eventId, "Sample Event", new Date(), new Date(), "Sample Location", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
