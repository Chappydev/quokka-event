package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;

import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.entrant.EventManager;
import com.example.quokka_event.models.event.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class EntrantActivity extends AppCompatActivity {

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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.event_waitlist_details);
        User currentUser = User.getInstance(this.getApplicationContext());

        eventTitle = findViewById(R.id.event_name_text);
        dateText = findViewById(R.id.date_text);
        timeText = findViewById(R.id.time_text);
        locationText = findViewById(R.id.location_text);
        organizerText = findViewById(R.id.organizer_text);

        // Set up date for signup
        Date testDate = new Date();
        // Set up deadline date
        Calendar cal = Calendar.getInstance();
        cal.setTime(testDate);
        cal.add(Calendar.DATE, 6);
        Date deadline = cal.getTime();
        Event event = new Event("01", "Test Event", testDate, deadline, "Edmonton",5,5,new ArrayList<ProfileSystem>(), new ArrayList<ProfileSystem>(), new ArrayList<ProfileSystem>());

        eventTitle.setText(event.getEventName());

        // set up time text
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String eventTime = timeFormat.format(event.getEventDate());timeText.setText(eventTime);
        timeText.setText(eventTime);

        locationText.setText(event.getEventLocation());

        // Seems like Event is missing organizer name.
        organizerText.setText("organizer");

        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy", Locale.getDefault());

        String dateTextFormatted = getString(R.string.date, testDate);
        dateText.setText(event.getEventDate().toString());




        event.addEntrantToWaitlist(currentUser.getProfile());
    }
}
