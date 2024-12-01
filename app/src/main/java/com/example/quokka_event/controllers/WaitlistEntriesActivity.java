package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;

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
    private TextView eventNameText;

    /**
     * Sets up the UI and loads the waitlist.
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
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

        eventNameText.setText(eventName + " - Waitlist");

        waitlistEntrants = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        waitlistRecyclerView.setLayoutManager(layoutManager);
        adapter = new WaitlistEntriesAdapter(waitlistEntrants);
        waitlistRecyclerView.setAdapter(adapter);

        loadWaitlistedUsers();

        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetches and updates the waitlist.
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
}
