package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.MainActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.EventAdapter;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity displays a list of events in a RecyclerView by fetching data from the database.
 * @author Soaiba
 */
public class MyEventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private Map<String, String> eventStatusMap = new HashMap<>();
    private User user;
    private String userId;

    @Override
    /**
     * This method initializes the RecyclerView.
     * Fetches events from database.
     * Handles back button activity.
     * @author Soaiba
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_events_page);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.event_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(eventList, eventStatusMap, event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("event_id", event.getEventID());
            intent.putExtra("event_name", event.getEventName());
            intent.putExtra("event_date", event.getEventDate().getTime());
            intent.putExtra("event_location", event.getEventLocation());
            intent.putExtra("status", eventStatusMap.get(event.getEventID()));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Get user
        user = User.getInstance(this);
        userId = user.getDeviceID();

        // Get all events from database
        getEvents();

        // Get user's events from database
        //getUserEvents();;

        // Handle back button activity
        Button backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> goToLandingPage());
    }

    /**
     * This method gets all events from the database and updates the RecyclerView.
     * @author Soaiba
     */
    private void getEvents() {
        DatabaseManager.getInstance(this).getEventList(new DbCallback() {
            @Override
            /**
             * This method populates list with data from the database.
             * @author Soaiba
             * @param data data from the database.
             */
            public void onSuccess(Object data) {
                List<Map<String, Object>> eventDataList = (List<Map<String, Object>>) data;
                eventList.clear();

                // Get event data for wach event
                for (int i = 0; i < eventDataList.size(); i++) {
                    Map<String, Object> eventData = eventDataList.get(i);

                    // Fields
                    String eventId = (String) eventData.get("eventId");
                    String eventName = (String) eventData.get("eventName");
                    Date eventDate = ((com.google.firebase.Timestamp) eventData.get("eventDate")).toDate();
                    Date registrationDeadline = ((com.google.firebase.Timestamp) eventData.get("registrationDeadline")).toDate();
                    String eventLocation = (String) eventData.get("eventLocation");
                    int maxParticipants = ((Long) eventData.get("maxParticipants")).intValue();
                    int maxWaitlist = ((Long) eventData.get("maxWaitlist")).intValue();

                    Event event = new Event(eventId, eventName, eventDate, registrationDeadline, eventLocation,
                            maxParticipants, maxWaitlist, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                    eventList.add(event);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            /**
             * This method tells you if there is an error getting data from database
             * @author Soaiba
             * @param e exception encountered during the database query.
             */
            public void onError(Exception e) {
                Log.e("MyEventsActivity", "Failed to get database events", e);
            }
        });
    }

    /**
     * This method gets user's enrollments.
     * @author Soaiba
     */
    private void getUserEvents() {
        getUserEnrolls(userId);
    }

    /**
     * This method gets user's enrollments and gets those events.
     * @param userId the ID of the user we need to get events for.
     * @author Soaiba
     */
    private void getUserEnrolls(String userId) {
        DatabaseManager.getInstance(this).getUserEventList(userId, new DbCallback() {
            @Override
            /**
             * This method gets enrollments for user and gets those events.
             * @author Soaiba
             * @param data data from the database.
             */
            public void onSuccess(Object data) {
                List<Map<String, Object>> enrollDataList = (List<Map<String, Object>>) data;
                List<String> eventIds = new ArrayList<>();
                eventStatusMap.clear();

                // Going through enrolls in database and getting all the eventIDs that the user has interacted with
                for (int i = 0; i < enrollDataList.size(); i++) {
                    Map<String, Object> enrollData = enrollDataList.get(i);
                    String eventId = (String) enrollData.get("eventId");
                    String status = (String) enrollData.get("status");
                    eventIds.add(eventId);
                    eventStatusMap.put(eventId, status);
                }
                getEventsByIds(eventIds);
            }

            @Override
            /**
             * This method tells you if there is an error getting user enrolls.
             * @author Soaiba
             * @param e exception encountered during the database query.
             */
            public void onError(Exception e) {
                Log.e("MyEventsActivity", "Failed to get user enrolls", e);
            }
        });
    }

    /**
     * This method gets events for the list of event IDs.
     * @param eventIds list of events to get details for.
     * @author Soaiba
     */
    private void getEventsByIds(List<String> eventIds) {
        DatabaseManager.getInstance(this).getEventsByIds(eventIds, new DbCallback() {
            @Override
            /**
             * This method gets events for the list of event IDs.
             * @author Soaiba
             * @param eventData data from the database.
             */
            public void onSuccess(Object eventData) {
                List<Map<String, Object>> eventDataList = (List<Map<String, Object>>) eventData;
                eventList.clear();

                // Get info for each event
                for (int i = 0; i < eventDataList.size(); i++) {
                    Map<String, Object> eventDataMap = eventDataList.get(i);
                    String eventId = (String) eventDataMap.get("eventId");
                    String eventName = (String) eventDataMap.get("eventName");
                    Date eventDate = ((com.google.firebase.Timestamp) eventDataMap.get("eventDate")).toDate();
                    Date registrationDeadline = ((com.google.firebase.Timestamp) eventDataMap.get("registrationDeadline")).toDate();
                    String eventLocation = (String) eventDataMap.get("eventLocation");
                    int maxParticipants = ((Long) eventDataMap.get("maxParticipants")).intValue();
                    int maxWaitlist = ((Long) eventDataMap.get("maxWaitlist")).intValue();

                    // Status for event i
                    String status = eventStatusMap.get(eventId);

                    // Add the event to eventList
                    Event event = new Event(eventId, eventName, eventDate, registrationDeadline, eventLocation,
                            maxParticipants, maxWaitlist, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                    eventList.add(event);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            /**
             * This method tells you if there is an error getting events by ID.
             * @author Soaiba
             * @param e exception encountered during the database query.
             */
            public void onError(Exception e) {
                Log.e("MyEventsActivity", "Failed to get events by IDs", e);
            }
        });
    }

    /**
     * This method navigates the user to the landing page.
     * @see <a href="https://medium.com/@snaresh22/mastering-android-app-navigation-with-intent-flags-36f84409432b.">...</a>
     * @author Soaiba
     */
    private void goToLandingPage() {
        Intent intent = new Intent(MyEventsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
