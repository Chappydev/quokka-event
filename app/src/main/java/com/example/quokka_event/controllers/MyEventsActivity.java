package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.MainActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.models.event.EventAdapter;
import com.example.quokka_event.models.event.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This activity displays a list of events in a RecyclerView.
 * @author Soaiba
 */
public class MyEventsActivity extends AppCompatActivity {
    @Override
    /**
     * This method displays the events in a RecyclerView.
     * Handles back button activity.
     * @author Soaiba
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_events_page);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.event_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EventAdapter adapter = getEventAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize button
        // Handle button activity
        Button backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> goToLandingPage());
    }

    @NonNull
    /**
     * This method creates an adapter with a list of events and sets a click listener for them.
     * Navigates user to details page of chosen event.
     * @author Soaiba
     * @return EventAdapter new adapter that has list of events.
     */
    private EventAdapter getEventAdapter() {
        List<Event> eventList = getEventList();
        return new EventAdapter(eventList, event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("event_id", event.getEventID());
            intent.putExtra("event_name", event.getEventName());
            intent.putExtra("event_date", event.getEventDate().getTime());
            intent.putExtra("event_location", event.getEventLocation());
            startActivity(intent);
        });
    }

    // sample list for testing
    private List<Event> getEventList() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("1", "Sample Event", new Date(2023 - 1900, 8, 21), new Date(), "Location", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        events.add(new Event("2", "Sample Event2", new Date(2024 - 1900, 9, 2), new Date(), "Location2", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        return events;
    }

    /**
     * This method navigates user to the landing page.
     * @see <a href="https://medium.com/@snaresh22/mastering-android-app-navigation-with-intent-flags-36f84409432b.">...</a>
     * @author Soaiba
     */
    private void goToLandingPage() {
        Intent intent = new Intent(MyEventsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
