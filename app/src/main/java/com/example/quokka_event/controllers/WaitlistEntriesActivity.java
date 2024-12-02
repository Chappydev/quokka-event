package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.controllers.waitlistlottery.OrganizerDrawFragment;
import com.example.quokka_event.models.event.LotteryChecker;
import com.example.quokka_event.views.Toolbar;

import java.util.ArrayList;
import java.util.Map;

/**
 * Displays the waitlist for a specific event.
 */
public class WaitlistEntriesActivity extends AppCompatActivity {
    private RecyclerView waitlistRecyclerView;
    private ArrayList<Map<String, Object>> waitlistEntrants;
    private WaitlistEntriesAdapter adapter;
    private DatabaseManager db;
    private String eventId;
    private String eventName;
    private TextView eventNameText;
    private long maxSlots = 0;

    /**
     * Sets up the UI and loads the waitlist.
     *
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlist_entries);

        db = DatabaseManager.getInstance(this);

        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");

        waitlistRecyclerView = findViewById(R.id.waitlist_recycler_view);
        eventNameText = findViewById(R.id.event_name_text);
        eventNameText.setText(eventName + " - Waitlist");

        waitlistEntrants = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        waitlistRecyclerView.setLayoutManager(layoutManager);
        adapter = new WaitlistEntriesAdapter(waitlistEntrants);
        waitlistRecyclerView.setAdapter(adapter);

        loadWaitlistedUsers();
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
    void getSlot() {
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

    void runLottery() {
        Log.d("Lottery", "runLottery: running" + eventId + " " + eventName + " " + maxSlots);
        Intent intent = new Intent(this, LotteryChecker.class);
        Log.d("Lottery", "runLottery: running" + eventId + " " + eventName + " " + maxSlots);
        intent.putExtra("eventId", eventId);
        intent.putExtra("eventName", eventName);
        intent.putExtra("maxParticipants", maxSlots);
        intent.putExtra("lotteryType", "regular");
        LotteryChecker lotteryChecker = new LotteryChecker();
        lotteryChecker.onReceive(this, intent);
    }
}