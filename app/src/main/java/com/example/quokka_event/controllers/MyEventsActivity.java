package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.MainActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.EventAdapter;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.ArrayList;
import java.util.Date;
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
        adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("event_id", event.getEventID());
            intent.putExtra("event_name", event.getEventName());
            intent.putExtra("event_date", event.getEventDate().getTime());
            intent.putExtra("event_location", event.getEventLocation());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Get events from database
        getEvents();

        // Handle back button activity
        Button backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> goToLandingPage());
    }

    /**
     * This method gets events from the database and updates the RecyclerView.
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

                // Get event data from database
                // Convert timestamp to date @see https://stackoverflow.com/questions/52247445/how-do-i-convert-a-firestore-date-timestamp-to-a-js-date
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

                    // Create the event and add it to the list
                    Event event = new Event(eventId, eventName, eventDate, registrationDeadline, eventLocation, maxParticipants, maxWaitlist, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
