package com.example.quokka_event;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.controllers.NotificationPageAdapter;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.Notification;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity for displaying entrant's notifications
 */
public class NotificationPageActivity extends AppCompatActivity {
    private RecyclerView notificationsRecyclerView;
    private NotificationPageAdapter notificationPageAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private DatabaseManager db;
    private FirebaseAuth auth;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_notification_page);

        // Set up database
        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();

        // Set up RecyclerView
        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationPageAdapter = new NotificationPageAdapter(notificationList);
        notificationsRecyclerView.setAdapter(notificationPageAdapter);

        // Set up back button
        backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> {
            Log.d("UI", "Back button clicked, finishing activity");
            finish();
        });

        // Load notifications
        Log.d("Lifecycle", "Starting to load notifications...");
        loadNotifications();
    }

    private void loadNotifications() {
        // Log current user
        if (auth.getCurrentUser() == null) {
            Log.e("NotificationError", "No user is signed in.");
            return;
        }

        // Log deviceId
        String deviceId = auth.getCurrentUser().getUid();
        Log.d("DeviceId", "Device ID: " + deviceId);

        // Log when starting the database query
        Log.d("Database", "Fetching notifications for deviceId: " + deviceId);

        db.getUserNotifications(deviceId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("Database", "Query successful, processing notifications...");

                // Log query result
                List<Map<String, Object>> notifications = (List<Map<String, Object>>) result;
                Log.d("Notifications", "Fetched notifications: " + notifications);

                for (Map<String, Object> notifData : notifications) {
                    String notifId = (String) notifData.get("notificationId");
                    String title = (String) notifData.get("notifTitle");
                    String message = (String) notifData.get("notifMessage");
                    String eventId = (String) notifData.get("eventId");

                    Notification notification = new Notification(notifId, title, message, eventId, null, null);
                    notificationList.add(notification);
                }

                // Log notification list size before updating adapter
                Log.d("Adapter", "Notification list size: " + notificationList.size());

                // Update adapter
                notificationPageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Log error
                Log.e("Notifications", "Error fetching notifications", e);
            }
        });
    }

}
