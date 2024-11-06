package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.R;

public class AdminLandingPageActivity extends AppCompatActivity {

    Button browse_profile_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_landing_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseManager db = DatabaseManager.getInstance(this);
        browse_profile_button = findViewById(R.id.browse_profile_button);

        browse_profile_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Browse Profiles
             * @param view
             */
            @Override
            public void onClick(View view) {

            }
        });

    }
}
