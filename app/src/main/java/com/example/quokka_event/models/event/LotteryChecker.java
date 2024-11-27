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
 * This class will call onReceive once a deadline is reached
 */
public class LotteryChecker extends BroadcastReceiver {
    /**
     * When the deadline is passed update database and roll lottery using AlarmManager
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventid = intent.getStringExtra("eventid");
        DatabaseManager db = DatabaseManager.getInstance(context);
        final int[] spots = {intent.getIntExtra("eventSpots", 0)};
        db.getParticipants(eventid, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof ArrayList){
                    ArrayList<Map<String, Object>> participants = (ArrayList<Map<String, Object>>) result;
                    spots[0] = spots[0] - participants.size();
                }
            }

            @Override
            public void onError(Exception exception) {

            }
        });
        db.getWaitlistEntrants(eventid, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof ArrayList){
                    ArrayList<Map<String, Object>> waitlistEntrants = (ArrayList<Map<String, Object>>) result;
                    Lottery lottery = new Lottery();
                    ArrayList<Map<String, Object>> lotteryWinners = lottery.runLottery(spots[0], waitlistEntrants);
                    for (Map<String,Object> user : lotteryWinners){
                        String userId = (String) user.get("deviceId");
                        db.updateEventStatus(eventid, userId, "Invited", new DbCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                // Put notification here???
                            }

                            @Override
                            public void onError(Exception exception) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onError(Exception exception) {
                Log.e("DB ERROR", "couldn't get waitlist");
            }
        });

    }
}
