package com.example.quokka_event;

import android.content.Intent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.ViewPagerAdapter;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.EventTabsActivity;
import com.example.quokka_event.models.event.OverviewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;

public class OrganizerEventsPageActivity extends AppCompatActivity implements OverviewFragment.overviewEditListener {
    Button addButton;
    Button saveButton;
    Event event = new Event();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events_page);

        // Switch the activity to the NotificationPageActivity when the bell icon is clicked
        final ImageButton bellButton = findViewById(R.id.bell);
        bellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(OrganizerEventsPageActivity.this, NotificationPageActivity.class);
                startActivity(showActivity);
            }
        });

        addButton = findViewById(R.id.add_button_bottom);
        saveButton = findViewById(R.id.save_button);
        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.back_button_bottom);

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
                //Intent intent = new Intent(OrganizerEventsPageActivity.this, CreateEventActivity.class);
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send info to database
            }
        });

    }

    @Override
    public void setEventName(String eventTitle) {
        event.setEventName(eventTitle);
    }

    @Override
    public void setEventDate(Date eventDate) {
        event.setEventDate(eventDate);
    }
}