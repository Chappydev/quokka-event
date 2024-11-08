package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;

import java.util.ArrayList;
import java.util.Map;

public class MyEventsPageActivity extends AppCompatActivity {
    private DatabaseManager db;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_my_events_page);

        // Set up a click listener for the back button
        Button backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Getting events list for user
        user = User.getInstance(this);
        db = DatabaseManager.getInstance(this);
        db.getUserEventList(user.getDeviceID(), new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<Map<String, Object>> events = (ArrayList<Map<String, Object>>) result;
                for (Map<String, Object> enrolled:
                     events) {
                    Map<String, Object> event = (Map<String, Object>) enrolled.get("event");
                    if (event != null) {
                        Log.d("DB", "onSuccess: " + event.get("eventName"));
                    }
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("DB", "onError: ", exception);
            }
        });
        // End of getting events list for user
    }

}
