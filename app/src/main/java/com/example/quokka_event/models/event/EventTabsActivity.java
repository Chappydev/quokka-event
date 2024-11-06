package com.example.quokka_event.models.event;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.NotificationPageActivity;
import com.example.quokka_event.OrganizerEventsPageActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;

public class EventTabsActivity extends AppCompatActivity implements OverviewFragment.overviewEditListener, DetailsFragment.detailsListener {
    Button saveButton;
    Event event;

    /**
     * Setup EventTabsActivity. Have three tabs for setting/display event details.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_tabs);
        event = new Event();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Set up the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        saveButton = findViewById(R.id.savebutton);

        // Connect TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Details");
                    break;
                case 2:
                    tab.setText("QR Code");
                    break;
            }
        }).attach();

        // Switch the activity to the NotificationPageActivity when the bell icon is clicked
        final ImageButton bellButton = findViewById(R.id.bell);
        bellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(EventTabsActivity.this, NotificationPageActivity.class);
                startActivity(showActivity);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Save event and send to database.
             * @param view
             */
            @Override
            public void onClick(View view) {
                // send event to database;

            }
        });
    }

    /**
     * Called from listener from overviewfragment.java to set event name
     * @param eventTitle
     */
    @Override
    public void setEventName(String eventTitle) {
        event.setEventName(eventTitle);
    }

    /**
     * Called from listener from DetailsFragment.java to set event date
     * @param eventDate
     */
    @Override
    public void setEventDate(Date eventDate) {
        event.setEventDate(eventDate);
    }

    /**
     * Called from listener in DetailsFragment.java
     * @param waitlistCap
     * @param partCap
     */
    @Override
    public void setCapacity(int waitlistCap, int partCap) {
        event.setMaxWaitlist(waitlistCap);
        event.setMaxParticipants(partCap);
    }
}

