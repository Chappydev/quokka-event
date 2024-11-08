package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.R;

/**
 * Landing page for admins.
 */
public class AdminLandingPageActivity extends AppCompatActivity {

    Button browseProfileButton;
    Button browseEventButton;
    /**
     * Create landing page for admins.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_landing_page);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        browseProfileButton = findViewById(R.id.browse_profile_button);
        browseEventButton = findViewById(R.id.browse_events_button);

        browseProfileButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Browse all profiles from database. start {@link BrowseProfilesActivity }
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent showActivity = new Intent(AdminLandingPageActivity.this, BrowseProfilesActivity.class);
                startActivity(showActivity);
            }
        });

        browseEventButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Launch browse events activity for admins when browse event button is clicked.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent showActivity = new Intent(AdminLandingPageActivity.this, AdminBrowseEventsActivity.class);
                startActivity(showActivity);
            }
        });

    }
}
