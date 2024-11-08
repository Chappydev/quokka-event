package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
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
    private Event event;
    private TextView eventTitle;
    private TextView dateText;
    private TextView timeText;
    private TextView locationText;
    private TextView organizerText;
    private EventManager eventManager;
    private Button joinButton;
    private Button exitButton;

    //TODO SHOULD SWITCH TO THIS ACTIVITY FROM QR CODE
    @Override
    /**
     * This method displays event details.
     * Handles join button activity.
     * @author Saimon
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_waitlist_details);

        User currentUser = User.getInstance(this.getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = new Event();
            event.setEventDate((Date) extras.get("eventDate"));
            event.setEventLocation((String) extras.get("eventLocation"));
            event.setEventID((String) extras.get("id"));
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

//        // Set up date for signup
//        Date testDate = new Date();
//        // Set up deadline date
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(testDate);
//        cal.add(Calendar.DATE, 6);
//        Date deadline = cal.getTime();

        eventTitle.setText(event.getEventName());

        // set up time text
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String eventTime = timeFormat.format(event.getEventDate());
        timeText.setText(eventTime);

        locationText.setText(event.getEventLocation());

        // TODO: Add facility details on this page
        organizerText.setText("organizer");

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy", Locale.getDefault());

//        String dateTextFormatted = getString(R.string.date, testDate);
        dateText.setText(event.getEventDate().toString());

        joinButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When join button is clicked add the user to the event's waitlist using Event.addEntrantToWaitlist()
             * @author Saimon
             * @param view
             */
            @Override
            public void onClick(View view) {
                event.addEntrantToWaitlist(currentUser.getProfile());
            }
        });
    }
}
