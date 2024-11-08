package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.controllers.EventTabsActivity;
import com.example.quokka_event.views.OrganizerEventsAdapter;

import java.util.ArrayList;

public class OrganizerEventsPageActivity extends AppCompatActivity {
    Button addButton;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events_page);

        // Switch the activity to the NotificationPageActivity when the bell icon is clicked
        final ImageButton bellButton = findViewById(R.id.bell);
        bellButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Launch the notifications page
             * @param view
             */
            public void onClick(View view) {
                Intent showActivity = new Intent(OrganizerEventsPageActivity.this, NotificationPageActivity.class);
                startActivity(showActivity);
            }
        });
        ArrayList<Event> eventList = new ArrayList<Event>();
        OrganizerEventsAdapter customAdapter = new OrganizerEventsAdapter(eventList);
        RecyclerView recyclerView = findViewById(R.id.organizer_events_recycler);
        recyclerView.setAdapter(customAdapter);

        addButton = findViewById(R.id.add_button_bottom);
        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.delete_event_button_admin);

        backButton.setOnClickListener(v -> {
            finish();
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Launch CreateEventActivity to create event
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent showActivity = new Intent(OrganizerEventsPageActivity.this, EventTabsActivity.class);
                startActivity(showActivity);
            }
        });

        // Set up a click listener for the event view button
        Button eventViewButton = findViewById(R.id.event_view_button);
        eventViewButton.setOnClickListener(v->{
            Intent showActivity = new Intent(OrganizerEventsPageActivity.this, EventTabsActivity.class);
            startActivity(showActivity);
        });


    }

}