package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.LotteryChecker;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

/**
 * Event tabs for admin browsing.
 */
public class AdminEventTabsActivity extends AppCompatActivity {
    Map<String, Object> event_details;
    Button backButton;
    Button deleteButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_tabs);
        backButton = findViewById(R.id.cancel_button);
        deleteButton = findViewById(R.id.delete_button);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            event_details = (Map<String, Object>) extras.get("event");
        }
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        DatabaseManager db = DatabaseManager.getInstance(this);
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Return to event list when clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Allow the admin to delete the event from the database when button is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                String eventId = (String)event_details.get("eventId");
                db.deleteEvent(eventId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        returnToEventList();
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            }
        });

        // Add debug lottery button
        Button debugLotteryButton = findViewById(R.id.debug_lottery_button);
        debugLotteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLottery();
            }
        });

    }

    /**
     * Set event details
     * @return
     */
    public Map<String, Object> getEventDetails(){
        return event_details;
    }

    /**
     * Return to events list for admins once event is deleted.
     */
    void returnToEventList(){
        Intent intent = new Intent(this, AdminBrowseEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void testLottery() {
        // Get event details from your existing event_details map
        String eventId = (String) event_details.get("eventId");
        String eventName = (String) event_details.get("eventName");
        long maxParticipants = (long) event_details.get("maxParticipants");

        // Create intent similar to what AlarmManager would create
        Intent testIntent = new Intent();
        testIntent.putExtra("eventId", eventId);
        testIntent.putExtra("eventName", eventName);
        testIntent.putExtra("maxParticipants", (int) maxParticipants);

        // Run the lottery
        LotteryChecker checker = new LotteryChecker();
        checker.onReceive(getApplicationContext(), testIntent);

        // Show a toast so we know it started
        Toast.makeText(this, "Running lottery test...", Toast.LENGTH_SHORT).show();
    }
}
