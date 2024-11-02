package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quokka_event.models.event.EventAdapter;
import com.example.quokka_event.models.event.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_events_page);

        RecyclerView recyclerView = findViewById(R.id.event_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Event> eventList = getEventList();
        EventAdapter adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("event_id", event.getEventID());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private List<Event> getEventList() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("1", "Sample Event", new Date(), new Date(), "Location", 100, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        // Populate with more events as needed
        return events;
    }
}
