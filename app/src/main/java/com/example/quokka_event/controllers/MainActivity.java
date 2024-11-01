package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton profileImage;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_landing_page);

        profileImage = findViewById(R.id.profile);
        toolbar = findViewById(R.id.topToolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            /**
             * When the profile image is clicked open CreateProfileActivity.java
             * @param view
             * */
            @Override
            public void onClick(View view) {
                switchToProfileActivity();

            }
        });
    }
    /**
     * Function to switch to CreateProfileActivity.java
     *
     * */
    private void switchToProfileActivity(){
        Intent switchActivity = new Intent(this, CreateProfileActivity.class);
        startActivity(switchActivity);
    }
}