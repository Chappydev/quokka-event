package com.example.quokka_event.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.NotificationPageActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.OverviewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.context.AttributeContext;

import java.util.Date;
import java.util.Objects;

public class EventTabsActivity extends AppCompatActivity implements OverviewFragment.overviewEditListener, DetailsFragment.detailsListener {
    Button saveButton;
    Button cancelButton;
    Event event;
    FirebaseAuth auth;
    /**
     * Setup EventTabsActivity. Have three tabs for setting/display event details.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_tabs);
        event = new Event();
        Date currentDate = new Date();
        event.setEventName("Event");
        event.setEventLocation("Location");
        event.setEventDate(currentDate);
        event.setRegistrationDeadline(currentDate);
        event.setDescription("");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        auth = FirebaseAuth.getInstance();
        DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());

        // Set up the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        saveButton = findViewById(R.id.savebutton);
        cancelButton = findViewById(R.id.cancelbutton);

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
            @Override
            public void onClick(View view) {
                String deviceId = auth.getCurrentUser().getUid();
                db.addEvent(event, deviceId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Log.d("DB", "added Event: " + event.getEventName() + " to database");
                        Toast.makeText(EventTabsActivity.this,
                                "Event created successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("DB", "Error creating event: ", exception);
                        Toast.makeText(EventTabsActivity.this,
                                "Error creating event",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> {
            finish();
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
     * Set event location called from a listener from DetailsFragment.java
     * @param location
     */
    @Override
    public void setLocation(String location) {
        event.setEventLocation(location);
    }

    /**
     * Set event deadline called from a listener in DetailsFragment.java
     * @param deadline
     */
    @Override
    public void setDeadline(Date deadline) {
        event.setRegistrationDeadline(deadline);
    }

    @Override
    public void setDescription(String description) { event.setDescription(description); }

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

