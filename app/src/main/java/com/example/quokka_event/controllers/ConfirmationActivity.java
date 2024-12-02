package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

/**
 * This activity displays a message based on what button the user interacted with from the events detail page.
 * @author Soaiba
 */
public class ConfirmationActivity extends AppCompatActivity{
    @Override
    /**
     * This method displays a message based on message type passed from event details page.
     * Handles exit button activity.
     * @author Soaiba
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_confirmation);

        // Get data from intent
        String messageType = getIntent().getStringExtra("message_type");
        String eventName = getIntent().getStringExtra("event_name");

        TextView confirmationMessage = findViewById(R.id.confirmationMessage);

        // Display the message based on the message type
        if ("Accept".equals(messageType)) {
            confirmationMessage.setText("You have joined " + eventName + "!");
        } else if ("Deny".equals(messageType)) {
            confirmationMessage.setText("You denied invitation to " + eventName + " :(");
        } else if ("Leave".equals(messageType)) {
            confirmationMessage.setText("You have unjoined the waitlist to " + eventName + ".");
        } else if ("JoinWaitlist".equals(messageType)) {
            confirmationMessage.setText("You have joined the waitlist for " + eventName + "!");
        }

        /**
         * This method handles exit function activity.
         * Navigates user to event list page.
         * @see <a href="https://medium.com/@snaresh22/mastering-android-app-navigation-with-intent-flags-36f84409432b.">...</a>
         * @author Soaiba
         */
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmationActivity.this, MyEventsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
