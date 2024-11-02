package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class OrganizerEventsPageActivity extends AppCompatActivity {
    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_events_page);
        addButton = findViewById(R.id.add_button_bottom);
        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.back_button_bottom);

        backButton.setOnClickListener(v -> {
            finish();
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Launch CreateEventActivity to create event
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerEventsPageActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }

}