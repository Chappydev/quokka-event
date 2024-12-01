package com.example.quokka_event;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for displaying notification page
 */
public class NotificationPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_notification_page);

        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}