package com.example.quokka_event.models.event;

import android.content.Context;
import android.util.Log;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * This class runs lottery to randomly select users from waitlist to invite to event.
 * @author Soaiba
 */
public class Lottery {
    private static final String TAG = "Lottery";

    /**
     * Runs lottery to fill the available spots with users from the waitlist.
     * @author Soaiba
     * @param numSpots the number of spots available.
     * @param waitlist the list of users in waitlist
     * @return ArrayList of selected participants
     */
    public ArrayList<Map<String, Object>> runLottery(int numSpots, ArrayList<Map<String, Object>> waitlist) {
        ArrayList<Map<String, Object>> selectedParticipants = new ArrayList<>();

        // Input validation
        if (numSpots <= 0) {
            Log.d(TAG, "No spots available for lottery");
            return selectedParticipants;
        }

        if (waitlist == null || waitlist.isEmpty()) {
            Log.d(TAG, "Waitlist is empty");
            return selectedParticipants;
        }

        // Make copy of waitlist to avoid modifying original
        ArrayList<Map<String, Object>> waitlistCopy = new ArrayList<>(waitlist);

        // Shuffle waitlist to randomize users
        Collections.shuffle(waitlistCopy);

        // Add randomized users from waitlist to selected users list
        int selectCount = Math.min(numSpots, waitlistCopy.size());
        for (int i = 0; i < selectCount; i++) {
            Map<String, Object> selectedUser = waitlistCopy.get(i);
            selectedParticipants.add(selectedUser);
        }

        return selectedParticipants;
    }

    /**
     * Checks if a lottery should be run based on event deadline
     * @param event the event to check
     * @return true if lottery should be run
     */
    public boolean shouldRunLottery(Event event) {
        Date currentDate = new Date();
        return currentDate.after(event.getRegistrationDeadline());
    }

    /**
     * Calculates number of available spots in event
     * @param event the event to check
     * @param currentParticipants list of current participants
     * @return number of available spots
     */
    public int calculateAvailableSpots(Event event, ArrayList<Map<String, Object>> currentParticipants) {
        if (event == null) {
            return 0;
        }

        int acceptedCount = 0;
        for (Map<String, Object> participant : currentParticipants) {
            if ("Accepted".equals(participant.get("status"))) {
                acceptedCount++;
            }
        }

        return event.getMaxParticipants() - acceptedCount;
    }

    /**
     * Filters waitlist to only include users with "Waiting" status
     * @param allUsers list of all users in event
     * @return filtered waitlist
     */
    public ArrayList<Map<String, Object>> getWaitingUsers(ArrayList<Map<String, Object>> allUsers) {
        ArrayList<Map<String, Object>> waitingUsers = new ArrayList<>();

        for (Map<String, Object> user : allUsers) {
            if ("Waiting".equals(user.get("status"))) {
                waitingUsers.add(user);
            }
        }

        return waitingUsers;
    }
}