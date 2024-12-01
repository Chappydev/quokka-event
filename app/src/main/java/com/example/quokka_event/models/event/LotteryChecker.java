package com.example.quokka_event.models.event;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.EventDetailsActivity;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.ArrayList;
import java.util.Map;

/**
 * executes lottery when event deadline is reached
 */
public class LotteryChecker extends BroadcastReceiver {
    private static final String TAG = "LotteryChecker";
    private static final String CHANNEL_ID = "lottery_notifications";

    /**
     * Creates notification channel for Android O and above
     * @reference https://developer.android.com/develop/ui/views/notifications/channels
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lottery Notifications";
            String description = "Notifications for event lottery results";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Sends notification to selected participant
     */
    private void sendInviteNotification(Context context, String userId, String eventId, String eventName) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra("event_id", eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Event Invitation")
                .setContentText("You've been invited to participate in " + eventName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(userId.hashCode(), builder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventId = intent.getStringExtra("eventId");
        String eventName = intent.getStringExtra("eventName");
        final int maxSpots = intent.getIntExtra("maxParticipants", 0);

        if (eventId == null || maxSpots <= 0) {
            Log.e(TAG, "Invalid event data received");
            return;
        }

        createNotificationChannel(context);
        DatabaseManager db = DatabaseManager.getInstance(context);

        db.getEnrolls(eventId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<Map<String, Object>> currentParticipants = (ArrayList<Map<String, Object>>) result;
                int acceptedCount = 0;

                // accepted participants
                for (Map<String, Object> participant : currentParticipants) {
                    if ("Accepted".equals(participant.get("status"))) {
                        acceptedCount++;
                    }
                }

                final int availableSpots = maxSpots - acceptedCount;
                if (availableSpots <= 0) {
                    Log.e(TAG, "No spots available for lottery");
                    return;
                }

                // Get waitlist and run lottery
                db.getWaitlistEntrants(eventId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        ArrayList<Map<String, Object>> waitlistEntrants = (ArrayList<Map<String, Object>>) result;

                        if (waitlistEntrants.isEmpty()) {
                            Log.e(TAG, "No users in waitlist");
                            return;
                        }

                        Lottery lottery = new Lottery();
                        ArrayList<Map<String, Object>> winners = lottery.runLottery(availableSpots, waitlistEntrants);

                        // Update status for winners
                        for (Map<String, Object> winner : winners) {
                            String userId = (String) winner.get("userId");
                            if (userId != null) {
                                db.updateEventStatus(eventId, userId, "Invited", new DbCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        // Send notification to winner
                                        sendInviteNotification(context, userId, eventId, eventName);
                                        Log.d(TAG, "Successfully invited user: " + userId);
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        Log.e(TAG, "Error updating status for user: " + userId, exception);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e(TAG, "Error getting waitlist entrants", exception);
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                Log.e(TAG, "Error getting current participants", exception);
            }
        });
    }
}