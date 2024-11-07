package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

/**
 * This class displays a message based on what button the user interacted with from the events detail page.
 * Handles activity for the exit button.
 * @author Soaiba
 */
public class ConfirmationActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_confirmation);

        String messageType = getIntent().getStringExtra("message_type");
        String eventName = getIntent().getStringExtra("event_name");

        TextView confirmationMessage = findViewById(R.id.confirmationMessage);

        // Display the message based on the message type
        if ("Accept".equals(messageType)) {
            confirmationMessage.setText("You have joined " + eventName + "!");
        } else if ("Deny".equals(messageType)) {
            confirmationMessage.setText("You denied invitation to " + eventName + " :(");
        } else if ("Cancel".equals(messageType)) {
            confirmationMessage.setText("You have unjoined the waitlist to " + eventName + ".");
        }

        /**
         * Method handles exit function activity.
         * Returns user to event list page.
         * @see https://medium.com/@snaresh22/mastering-android-app-navigation-with-intent-flags-36f84409432b
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
