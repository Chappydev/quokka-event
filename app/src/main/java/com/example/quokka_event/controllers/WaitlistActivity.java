package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.QrScannerPageActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.entrant.EventManager;
import com.example.quokka_event.models.event.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This activity displays information about events for users to waitlist for.
 * @author Saimon
 */
public class WaitlistActivity extends AppCompatActivity {
    private DatabaseManager db;
    private Event event;
    private TextView eventTitle;
    private TextView dateText;
    private TextView timeText;
    private TextView locationText;
    private TextView organizerText;
    private EventManager eventManager;
    private Button joinButton;
    private Button exitButton;

    /**
     * This method displays event details.
     * Handles join button activity.
     * @author Saimon
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_waitlist_details);

        User currentUser = User.getInstance(this.getApplicationContext());
        db = DatabaseManager.getInstance(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = new Event();
            event.setEventDate((Date) extras.get("eventDate"));
            event.setEventLocation((String) extras.get("eventLocation"));
            event.setEventID((String) extras.getString("eventId"));
            event.setEventName((String) extras.get("eventName"));
            event.setMaxParticipants((int) extras.get("maxParticipants"));
            event.setRegistrationDeadline((Date) extras.get("registrationDeadline"));
            event.setMaxWaitlist((int) extras.get("maxWaitlist"));
        }

        // Initialize views and buttons
        eventTitle = findViewById(R.id.event_name_text);
        dateText = findViewById(R.id.date_text);
        timeText = findViewById(R.id.time_text);
        locationText = findViewById(R.id.location_text);
        organizerText = findViewById(R.id.organizer_text);
        joinButton = findViewById(R.id.join_waitlist_button);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(event.getEventDate());

        // Display info
        eventTitle.setText(event.getEventName());
        dateText.setText("Date: " + formattedDate);
        timeText.setText("Time: " + formattedTime);
        locationText.setText("Location: " + event.getEventLocation());
        organizerText.setText("Organizer: TBD"); // TODO: Update once organizer data is available

        // Join button activity
        joinButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When join button is clicked add the user to the event's waitlist and route back to
             * MyEventsActivity
             * @author Chappydev
             * @param view
             */
            @Override
            public void onClick(View view) {
                db.joinWaitlist(event.getEventID(), User.getInstance(WaitlistActivity.this).getDeviceID(), new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Toast.makeText(WaitlistActivity.this, "You joined the waitlist for '" + event.getEventName() + "'", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WaitlistActivity.this, MyEventsActivity.class);
                        WaitlistActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("DB", "WaitlistActivity onError: ", exception);
                        Toast.makeText(WaitlistActivity.this, "Something went wrong adding you to the waitlist", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
