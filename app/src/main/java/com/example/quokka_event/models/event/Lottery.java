package com.example.quokka_event.models.event;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
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
    public ArrayList<Map<String, Object>> runLottery(long numSpots, ArrayList<Map<String, Object>> waitlist) {
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

        if (numSpots > waitlist.size()){
            numSpots = waitlist.size();
        }

        // Add randomized users from waitlist to selected users list
//        long selectCount = Math.min(numSpots, waitlistCopy.size());
        for (int i = 0; i < numSpots; i++) {
            Map<String, Object> selectedUser = waitlistCopy.get(i);
            selectedParticipants.add(selectedUser);
        }

        return selectedParticipants;
    }
}