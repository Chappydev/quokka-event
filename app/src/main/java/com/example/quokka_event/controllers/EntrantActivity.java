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

public class EntrantActivity extends AppCompatActivity {

    private TextView eventTitle;
    private TextView dateText;
    private TextView timeText;
    private TextView locationText;
    private TextView organizerText;

    private EventManager eventManager;

    private TextView waitlistJoinConfirmation;
    private Button joinButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.event_waitlist_details);
        User currentUser = User.getInstance(this.getApplicationContext());


        Date testDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(testDate);
        cal.add(Calendar.DATE, 6);
        Date deadline = cal.getTime();
        Event event = new Event("01", "Cool Event", testDate, deadline, "Edmonton",5,5,new ArrayList<ProfileSystem>(), new ArrayList<ProfileSystem>(), new ArrayList<ProfileSystem>());

        eventTitle.setText(event.getEventName());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String eventTime = timeFormat.format(event.getEventDate());
        dateText.setText(event.getEventDate().toString());
        timeText.setText(eventTime);
        locationText.setText(event.getEventLocation());
        organizerText.setText("organizer");

        event.addEntrantToWaitlist(currentUser.getProfile());
    }
}
