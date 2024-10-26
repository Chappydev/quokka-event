package com.example.quokka_event.models.event;

import com.example.quokka_event.models.User;
import com.example.quokka_event.models.entrant.EventManager;

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


    // The waitlist should not be arraylist of eventmanager. Since eventmanager does not
    // have any variables to differentiate two different eventmanager objects.
    private ArrayList<User> participantList;
    private ArrayList<User> waitList;
    private ArrayList<User> cancelledParticipants;

    // Constructor
    public Event(String eventID, String eventName, Date eventDate, Date registrationDeadline, String eventLocation, int maxParticipants, int maxWaitlist, ArrayList<User> participantList, ArrayList<User> waitList, ArrayList<User> cancelledParticipants) {
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

    public Event(String name, Date eventDate, Date registrationDeadline, String location, int maxSpots, int maxRegistration) {
    }

    public Event() {

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

    public ArrayList<User> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<User> participantList) {
        this.participantList = participantList;
    }

    public ArrayList<User> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<User> waitList) {
        this.waitList = waitList;
    }

    public ArrayList<User> getCancelledParticipants() {
        return cancelledParticipants;
    }

    public void setCancelledParticipants(ArrayList<User> cancelledParticipants) {
        this.cancelledParticipants = cancelledParticipants;
    }

    // Function checks if waitlist is full
    public boolean isWaitListFull() {
        return waitList.size() >= maxWaitlist;
    }

    // Function checks if the registration deadline has passed
    public boolean isDeadline() {
        Date currentDate = new Date();
        return currentDate.after(getRegistrationDeadline());
    }

    // Function adds Entrant to waitlist if waitlist is not full and registration deadline has not passed
    public boolean addEntrantToWaitlist(User user){
        if (!isWaitListFull() && !isDeadline()) {
            waitList.add(user);
            return true;
        }
        else {
            return false;
        }
    }
}
