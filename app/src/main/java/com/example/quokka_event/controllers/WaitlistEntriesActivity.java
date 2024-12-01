package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.controllers.waitlistlottery.OrganizerDrawFragment;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * The activity to display all waitlisted user in the event.
 */
public class WaitlistEntriesActivity extends AppCompatActivity {
    private RecyclerView waitlistRecyclerView;
    private ArrayList<Map<String, Object>> waitlistEntrants;
    private WaitlistEntriesAdapter adapter;
    private DatabaseManager db;
    private String eventId;
    private TextView eventNameText;
    private long maxSlots = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlist_entries);

        db = DatabaseManager.getInstance(this);

        eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("eventName");

        waitlistRecyclerView = findViewById(R.id.waitlist_recycler_view);
        eventNameText = findViewById(R.id.event_name_text);
        Button backButton = findViewById(R.id.back_button_bottom);
        Button drawButton = findViewById(R.id.draw_lottery_button);
        eventNameText.setText(eventName + " - Waitlist");

        waitlistEntrants = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        waitlistRecyclerView.setLayoutManager(layoutManager);
        adapter = new WaitlistEntriesAdapter(waitlistEntrants);
        waitlistRecyclerView.setAdapter(adapter);

        loadWaitlistedUsers();

        backButton.setOnClickListener(v -> finish());
        drawButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Show fragment for organizer drawing lottery
             * @param view
             */
            @Override
            public void onClick(View view) {
                getSlot();
            }
        });
    }

    /**
     * Get waitlisted users in event and display it in the recycler view.
     */
    private void loadWaitlistedUsers() {
        if (eventId == null) {
            Toast.makeText(this, "Error: No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.getWaitlistEntrants(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                waitlistEntrants.clear();
                waitlistEntrants.addAll((ArrayList<Map<String, Object>>) result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("Waitlist", "Error loading waitlist entries", e);
                Toast.makeText(WaitlistEntriesActivity.this,
                        "Error loading waitlist", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Get the number of entrants to check if the lottery can be drawn.
     */
    void getSlot(){
        db.getSingleEvent(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> event = (Map<String, Object>) result;
                Log.d("event get", event.get("maxParticipants").toString());
                maxSlots = (long) event.get("maxParticipants");
                Bundle bundle = new Bundle();
                bundle.putLong("slots", maxSlots);
                bundle.putString("eventId", eventId);
                OrganizerDrawFragment organizerDrawFragment = new OrganizerDrawFragment();
                organizerDrawFragment.setArguments(bundle);
                organizerDrawFragment.show(getSupportFragmentManager(), "frag");
            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }
}