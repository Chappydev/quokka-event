package com.example.quokka_event.models.event;

import com.example.quokka_event.models.ProfileSystem;

import java.util.ArrayList;
import java.util.Date;

public class Event {

    private String eventID;
    private String eventName;
    private Date eventDate;
    private Date registrationDeadline;
    private String eventLocation;
    //private Organizer organizer;

    private int maxParticipants;
    private int maxWaitlist;

    private ArrayList<ProfileSystem> participantList;
    private ArrayList<ProfileSystem> waitList;
    private ArrayList<ProfileSystem> cancelledParticipants;

    /**
     * Constructs an Event with specified attributes.
     * @author idk???
     * @param eventID                identifier for the event
     * @param eventName              name of the event
     * @param eventDate              date of the event
     * @param registrationDeadline   registration deadline for the event
     * @param eventLocation          location of the event
     * @param maxParticipants        maximum number of participants
     * @param maxWaitlist            maximum size of the waitlist
     * @param participantList        list of participants
     * @param waitList               list of waitlisted entrants
     * @param cancelledParticipants  list of cancelled participants
     */
    public Event(String eventID, String eventName, Date eventDate, Date registrationDeadline, String eventLocation, int maxParticipants, int maxWaitlist, ArrayList<ProfileSystem> participantList, ArrayList<ProfileSystem> waitList, ArrayList<ProfileSystem> cancelledParticipants) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.registrationDeadline = registrationDeadline;
        this.eventLocation = eventLocation;
        this.maxParticipants = maxParticipants;
        this.maxWaitlist = maxWaitlist;
        this.participantList = participantList;
        this.waitList = waitList;
        this.cancelledParticipants = cancelledParticipants;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public String getEventLocation() {
        return eventLocation;
    }


    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getMaxWaitlist() {
        return maxWaitlist;
    }

    public void setMaxWaitlist(int maxWaitlist) {
        this.maxWaitlist = maxWaitlist;
    }

    public ArrayList<ProfileSystem> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<ProfileSystem> participantList) {
        this.participantList = participantList;
        //TODO update database
    }

    public ArrayList<ProfileSystem> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<ProfileSystem> waitList) {
        this.waitList = waitList;
        //TODO update database
    }

    public ArrayList<ProfileSystem> getCancelledParticipants() {
        return cancelledParticipants;
    }

    public void setCancelledParticipants(ArrayList<ProfileSystem> cancelledParticipants) {
        this.cancelledParticipants = cancelledParticipants;
    }

    /**
     * Checks if the waitlist is full.
     * @author Simon and Soaiba
     * @return true if the waitlist size has reached the maximum capacity
     */
    public boolean isWaitListFull() {
        return waitList.size() >= maxWaitlist;
    }

    /**
     * Checks if the registration deadline has passed.
     * @author Soaiba
     * @return true if the current date is after the registration deadline.
     */
    public boolean isDeadline() {
        Date currentDate = new Date();
        return currentDate.after(getRegistrationDeadline());
    }

    /**
     * Adds an entrant to the waitlist if the waitlist is not full
     * and the registration deadline has not yet passed.
     * @param user the user to be added to the waitlist
     * @author Soaiba
     */
    public void addEntrantToWaitlist(ProfileSystem user){
        //TODO **** Update Database!! ****
        if (!isWaitListFull() && !isDeadline()) {
            waitList.add(user);
        }
    }
}
