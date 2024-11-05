package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

public class ConfirmationActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_confirmation);

        // Message to display
        String messageType = getIntent().getStringExtra("message_type");
        String eventName = getIntent().getStringExtra("event_name");
        TextView confirmationMessage = findViewById(R.id.confirmationMessage);

        // Set the message based on the message type
        if ("Accept".equals(messageType)) {
            confirmationMessage.setText("You have joined " + eventName + "!");
        } else if ("Deny".equals(messageType)) {
            confirmationMessage.setText("You denied invitation to " + eventName + " :(");
        } else if ("Cancel".equals(messageType)) {
            confirmationMessage.setText("You have unjoined the waitlist to " + eventName + ".");
        }

        // Exit button activity
        // Returns to event list page
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmationActivity.this, MyEventsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
