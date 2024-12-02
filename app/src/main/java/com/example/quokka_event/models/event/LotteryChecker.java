package com.example.quokka_event.models.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.ArrayList;
import java.util.Map;

/**
 * Executes lottery upon receiving a broadcast
 * @author speakerchef
 */
public class LotteryChecker extends BroadcastReceiver {
    private static final String TAG = "LotteryChecker";
    private static final String TYPE_REGULAR = "regular";
    private static final String TYPE_REPLACEMENT = "replacement";

    /**
     * Upon receiving a broadcast, lottery is run based on available slots
     * and type of lottery
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventId = intent.getStringExtra("eventId");
        String eventName = intent.getStringExtra("eventName");
        String lotteryType = intent.getStringExtra("lotteryType");
        final long maxSpots = intent.getLongExtra("maxParticipants", 0);

        if (eventId == null || maxSpots <= 0) {
            Log.e(TAG, "Invalid event data received" + eventId + maxSpots);
            return;
        }
        if (!lotteryType.equals(TYPE_REGULAR) && !lotteryType.equals(TYPE_REPLACEMENT)){
            Log.e(TAG, "onReceive: Invalid lottery type" + lotteryType );
            return;
        }
        
        DatabaseManager db = DatabaseManager.getInstance(context);

        db.getEnrolls(eventId, new DbCallback() {
            /**
             * Gets all entrants to find those who've already been invited
             * @author speakerchef
             * @param result
             */
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

                final long availableSpots = maxSpots - acceptedCount;
                final long spotsToFill = lotteryType.equals(TYPE_REGULAR) ? availableSpots : 1;

                Log.d(TAG, "Max slots: " + spotsToFill);
                if (availableSpots <= 0) {
                    Log.e(TAG, "No spots available for lottery");
                    return;
                }

                db.getWaitlistEntrants(eventId, new DbCallback() {
                    /**
                     * Gets all entrants who are waiting to be passed into the lottery
                     * @param result
                     */
                    @Override
                    public void onSuccess(Object result) {
                        ArrayList<Map<String, Object>> waitlistEntrants = (ArrayList<Map<String, Object>>) result;
                        Log.d(TAG, "onSuccess: eventId" + eventId + waitlistEntrants.toString());
                        if (waitlistEntrants.isEmpty()) {
                            Log.e(TAG, "No users in waitlist");
                            return;
                        }
                        Lottery lottery = new Lottery();
                        ArrayList<Map<String, Object>> winners = lottery.runLottery(spotsToFill, waitlistEntrants);

                        // Update status for winners
                        for (Map<String, Object> winner : winners) {
                            String userId = (String) winner.get("deviceId");
                            Log.d(TAG, "onSuccess: userId" + userId);
                            if (userId != null) {
                                db.updateEventStatus(eventId, userId, "Invited", new DbCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        // Send notification to winner
//                                        sendInviteNotification(context, userId, eventId, eventName);
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