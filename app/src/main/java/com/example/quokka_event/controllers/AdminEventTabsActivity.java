package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

/**
 * Displays event details for admins while navigating tabs
 */
public class AdminEventTabsActivity extends AppCompatActivity {
    Map<String, Object> event_details;
    private int  currIndex;
    Button backButton;
    Button deleteButton;

    /**
     * Initializes activity, sets up tab layout and UI
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_tabs);
        backButton = findViewById(R.id.cancel_button);
        deleteButton = findViewById(R.id.delete_button);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            event_details = (Map<String, Object>) extras.get("event");
            currIndex = (int) extras.get("index");
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

    }

    /**
     * Set event details
     * @return Map with all event details
     */
    public Map<String, Object> getEventDetails(){
        return event_details;
    }

    /**
     * Set event details
     * @return Map with all event details
     */
    public int getCurrentIndex(){
        return currIndex;
    }

    /**
     * Return to events list for admins once event is deleted.
     */
    void returnToEventList(){
        Intent intent = new Intent(this, AdminBrowseEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
