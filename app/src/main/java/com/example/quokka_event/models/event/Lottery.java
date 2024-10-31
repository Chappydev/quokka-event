package com.example.quokka_event.models.event;

import com.example.quokka_event.models.ProfileSystem;

import java.util.ArrayList;
import java.util.Collections;

// Class runs lottery to randomly select users from waitlist to invite to event
public class Lottery {
    private Event event;

    // Constructor
    public Lottery(Event event) {
        this.event = event;
    }

    /**
     * Runs lottery to fill the available spots with users from the waitlist.
     * @author Soaiba
     * @param numSpots the number of spots available
     */
    public void runLottery(int numSpots) {
        ArrayList<ProfileSystem> waitlist = event.getWaitList();
        ArrayList<ProfileSystem> selectedParticipants = event.getParticipantList();

        // Make sure there are spots available and there are users in the waitlist
        // Shuffle waitlist to randomize users
        if (numSpots>0 && !waitlist.isEmpty()) {
            Collections.shuffle(waitlist);

            // Add randomized users from waitlist to selected users list
            for (int i=0; i<numSpots && i<waitlist.size(); i++) {
                ProfileSystem selectedUser = waitlist.get(i);
                selectedParticipants.add(selectedUser);
            }

            // Update event class
            event.setParticipantList(selectedParticipants);
            waitlist.removeAll(selectedParticipants);
            event.setWaitList(waitlist);

            //TODO update database
            }
        }

    /**
     * Conducts the first lottery at the registration deadline.
     * @author Soaiba
     */
    public void firstlLottery() {
        if (event.isDeadline()) {
            int numSpots = event.getMaxParticipants();
            runLottery(numSpots);
        }
    }

    /**
     * Rerolls the lottery to fill open spots if a participant drops out.
     * @author Soaiba
     */
    public void rerollLottery() {
        int numSpots = event.getMaxParticipants() - event.getParticipantList().size();
        runLottery(numSpots);
    }
}
