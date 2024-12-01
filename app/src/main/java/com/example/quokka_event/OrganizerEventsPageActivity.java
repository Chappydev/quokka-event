package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.controllers.EventTabsActivity;
import com.example.quokka_event.views.OrganizerEventsAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

/**
 * Displays a list of organizer events. Allows managing and creation of events
 */
public class OrganizerEventsPageActivity extends AppCompatActivity {
    Button addButton;
    Button saveButton;
    private DatabaseManager db;
    private FirebaseAuth auth;
    ArrayList<Map<String,Object>> eventList;
    OrganizerEventsAdapter customAdapter;

    /**
     * Initializes the UI elements, sets up RecyclerView and handles button clicks.
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events_page);
        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();

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
        setupRecyclerView();

        addButton = findViewById(R.id.add_button_bottom);
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
                Intent showActivity = new Intent(OrganizerEventsPageActivity.this, EventTabsActivity.class);
                startActivity(showActivity);
            }
        });
        // Set up a click listener for the event view button
        Button eventViewButton = findViewById(R.id.event_view_button);
        eventViewButton.setOnClickListener(v->{
            Intent showActivity = new Intent(OrganizerEventsPageActivity.this, EventTabsActivity.class);
            startActivity(showActivity);

            // generate and display a qr code
        });
     }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        customAdapter = new OrganizerEventsAdapter(eventList);
        RecyclerView recyclerView = findViewById(R.id.organizer_events_recycler);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Add this line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));  // Optional: adds dividers
    }

    /**
     * Sets up RecyclerView to display a list of events
     */
    private void loadOrganizerEvents() {
        String deviceId = auth.getCurrentUser().getUid();
        db.getOrganizerEvents(deviceId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("Event", "onSuccess: " + result);
                eventList.clear();
                eventList.addAll((ArrayList<Map<String,Object>>)result);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception exception) {
                Log.e("Event", "onError: ", exception);
            }
        });
    }

    /**
     * When activity is resumed, loads the organizer events
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadOrganizerEvents();
    }
}