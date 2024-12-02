package com.example.quokka_event.models.event;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Use alarm manager to run lottery after deadline
 */
public class EventLotteryManager {
    public void deadlineLottery(Context context,Event event,long deadline){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LotteryChecker.class);
        intent.putExtra("eventid", event.getEventID());
        intent.putExtra("eventSpots", event.getMaxParticipants());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null){
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    deadline,
                    pendingIntent
            );
        }
    }
}
