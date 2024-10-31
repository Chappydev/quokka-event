package com.example.quokka_event;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

public class OrganizerEventsPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events_page);

        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

}