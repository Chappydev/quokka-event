package com.example.quokka_event.models.organizer;

import static android.app.PendingIntent.getActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.ViewPagerAdapterEventEntrant;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.EventAttendingFragment;
import com.example.quokka_event.models.event.EventCancelledFragment;
import com.example.quokka_event.models.event.EventInvitedFragment;
import com.example.quokka_event.models.event.EventWaitlistFragment;
import com.example.quokka_event.models.event.OverviewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Activity used when displaying the lists of different types of entrants for an event.
 * @author mylayambao
 * @since project part 4
 */

public class EventEntrantsPage extends AppCompatActivity implements EventWaitlistFragment.EventAttendingListener, OverviewFragment.overviewEditListener, EventAttendingFragment.EventAttendingListener {
    Button backButton;
    Button notifyButton;
    TextView eventName;
    Event event;
    private String eventId;
    private DatabaseManager db;
    private ArrayList<Map<String, Object>> selectedParticipants;
    private NotifySelectedFragment.NotifySelectedListener notifyListener;

    public void setNotifySelectedListener(NotifySelectedFragment.NotifySelectedListener listener) {
        this.notifyListener = listener;
    }


    /**
     * Sets up the activity when it is created.
     * @author mylayambo
     * @param savedInstanceState
     * @since project part 4
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("message","now displaying EventEntrantsActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_entrants_tabs);
        db = DatabaseManager.getInstance(this);

        eventId = getIntent().getStringExtra("eventId");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        backButton = findViewById(R.id.back_button);
        notifyButton = findViewById(R.id.notift_selected_button);

        ViewPagerAdapterEventEntrant adapter = new ViewPagerAdapterEventEntrant(this, eventId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Attending");
                    break;
                case 1:
                    tab.setText("Invited");
                    break;
                case 2:
                    tab.setText("Waitlist");
                    break;
                case 3:
                    tab.setText("Cancelled");
            }
        }).attach();

        // tab listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                String fragmentTag = "f" + position; // Match ViewPager2 fragment tag
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
                if (position == 0){
                    if (currentFragment instanceof EventAttendingFragment) {
                        ((EventAttendingFragment) currentFragment).clearSelection();
                    }
                }
                else if (position == 3){
                    if (currentFragment instanceof EventCancelledFragment) {
                        ((EventCancelledFragment) currentFragment).clearSelection();
                    }
                }
                else if (position == 1){
                    if (currentFragment instanceof EventInvitedFragment) {
                        ((EventInvitedFragment) currentFragment).clearSelection();
                    }
                }
                else if (position == 2){
                    if (currentFragment instanceof EventWaitlistFragment) {
                        ((EventWaitlistFragment) currentFragment).clearSelection();
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Clear selection in the fragment for the tab being unselected
                int position = tab.getPosition();
                clearSelectionInFragment(position);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        backButton.setOnClickListener(v -> finish());

        notifyButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Sends the notification when clicking the send button
             * @author mylayambao
             * @param view
             */
            @Override
            public void onClick(View view) {
                if (selectedParticipants == null || selectedParticipants.isEmpty()) {
                    Toast.makeText(EventEntrantsPage.this, "No participants selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("EventEntrantsPage", "Selected participants: " + selectedParticipants);

                NotifySelectedFragment notifySelectedFragment = NotifySelectedFragment.newInstance(eventId, selectedParticipants);
                notifySelectedFragment.setNotifySelectedListener(() -> {
                    // selected participants
                    selectedParticipants.clear();
                    int currentPosition = viewPager.getCurrentItem(); // get the current tab
                    String fragmentTag = "f" + currentPosition;
                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
                    if (currentFragment instanceof EventAttendingFragment) {
                        ((EventAttendingFragment) currentFragment).clearSelection();
                    }
                });
                notifySelectedFragment.show(getSupportFragmentManager(), "notify participants fragment");
            }
        });


    }

    /**
     * Helps clear the selected participants when the tab changes
     * @author mylayambao
     * @param position
     */
    private void clearSelectionInFragment(int position) {
        String fragmentTag = "f" + position;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment instanceof EventAttendingFragment) {
            ((EventAttendingFragment) fragment).clearSelection();
        }
    }

    @Override
    public void setEventName(String eventTitle) {}
    @Override
    public void setEventDate(Date eventDate) {}
    @Override
    public void setLocation(String location) {}
    @Override
    public void setDeadline(Date deadline) {}

    @Override
    public void setDescription(String description) {

    }

    @Override
    public void setImageUri(Uri imageUri) {

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

    }

    @Override
    public void onParticipantsSelected(ArrayList<Map<String, Object>> selectedParticipants) {
        this.selectedParticipants = selectedParticipants;
        Log.d("EventEntrantsPage", "Updated Selected Participants: " + selectedParticipants);
    }
}
