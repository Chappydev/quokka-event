package com.example.quokka_event.models.event;

import com.example.quokka_event.models.ProfileSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * This class runs lottery to randomly select users from waitlist to invite to event.
 * @author Soaiba
 */
public class Lottery {


    /**
     * Runs lottery to fill the available spots with users from the waitlist.
     * @author Soaiba
     * @param numSpots the number of spots available.
     */
    public ArrayList<Map<String, Object>> runLottery(int numSpots, ArrayList<Map<String, Object>> waitlist) {
        ArrayList<Map<String, Object>> selectedParticipants = new ArrayList<>();

        // Make sure there are spots available and there are users in the waitlist
        // Shuffle waitlist to randomize users
        if (numSpots > 0 && !waitlist.isEmpty()) {
            Collections.shuffle(waitlist);

            // Add randomized users from waitlist to selected users list
            for (int i = 0; i < numSpots && i < waitlist.size(); i++) {
                Map<String, Object> selectedUser = waitlist.get(i);
                selectedParticipants.add(selectedUser);
            }
            return selectedParticipants;

        }
        return new ArrayList<>();
    }

    /*
    /**
     * Rerolls the lottery to fill open spots if a participant drops out.
     * @author Soaiba

    public void rerollLottery() {
        int numSpots = event.getMaxParticipants() - event.getParticipantList().size();
        runLottery(numSpots);
    }

     */
}
