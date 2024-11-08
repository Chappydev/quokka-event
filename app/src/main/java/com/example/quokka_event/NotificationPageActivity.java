package com.example.quokka_event;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_notification_page);

        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.delete_event_button_admin);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

}