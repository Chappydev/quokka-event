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

        db = DatabaseManager.getInstance(this);
        auth = FirebaseAuth.getInstance();

        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationPageAdapter = new NotificationPageAdapter(notificationList);
        notificationsRecyclerView.setAdapter(notificationPageAdapter);

        backButton = findViewById(R.id.back_button_bottom);
        backButton.setOnClickListener(v -> {
            finish();
        });

        loadNotifications();
    }

    private void loadNotifications() {
        String deviceId = auth.getCurrentUser().getUid();
        Log.e("DeviceId", "Device ID: " + deviceId);

        db.getUserNotifications(deviceId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("Database", "Query successful, processing notifications...");
                List<Map<String, Object>> notifications = (List<Map<String, Object>>) result;
                Log.d("Notifications", "Fetched notifications: " + notifications);
                notificationList.clear();
                Log.d("Notifications", "Fetched notifications after clear: " + notifications);

                for (int i = 0; i < notifications.size(); i++) {
                    Map<String, Object> notifData = notifications.get(i);
                    String notifId = (String) notifData.get("notificationId");
                    String title = (String) notifData.get("notifTitle");
                    String message = (String) notifData.get("notifMessage");
                    String eventId = (String) notifData.get("eventId");
                    Notification notification = new Notification(notifId, title, message, eventId, null, null);
                    notificationList.add(notification);
                }
                Log.d("Notifications", "Notification list size: " + notificationList.size());
                notificationPageAdapter.notifyDataSetChanged();
                Log.d("Adapter", "Notified adapter. Item count: " + notificationPageAdapter.getItemCount());
            }

            @Override
            public void onError(Exception e) {
                Log.e("Notifications", "Error fetching notifications", e);
            }
        });
    }

}
