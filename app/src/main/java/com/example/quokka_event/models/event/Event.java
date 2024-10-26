package com.example.quokka_event.models.event;

import com.example.quokka_event.models.User;
import com.example.quokka_event.models.entrant.EventManager;

import java.util.ArrayList;
import java.util.Date;

public class Event {

    private String eventID;
    private String eventName;
    private Date eventDate;
    private String eventLocation;
    private Date registrationDeadline;
    //private Organizer organizer;

    private int maxParticipants;
    private int maxWaitlist;


    // The waitlist should not be arraylist of eventmanager. Since eventmanager does not
    // have any variables to differentiate two different eventmanager objects.
    private ArrayList<User> participantList;
    private ArrayList<User> waitList;
    private ArrayList<User> cancelledParticipants;


    // Constructor
    public Event(String eventID, String eventName, Date eventDate, String eventLocation, int maxParticipants, int maxWaitlist, ArrayList<User> participantList, Date registrationDeadline, ArrayList<User> waitList, ArrayList<User> cancelledParticipants) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.maxParticipants = maxParticipants;
        this.maxWaitlist = maxWaitlist;
        this.participantList = participantList;
        this.waitList = waitList;
        this.cancelledParticipants = cancelledParticipants;
        this.registrationDeadline = registrationDeadline;
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

    // check if waitlist is full
    public boolean isWaitListFull(){
        return waitList.size() >= maxWaitlist;
    }

    // add entrant to waitlist if waitlist is not ful
    public void addEntrantToWaitlist(User user){
        if (isWaitListFull() != true){
            waitList.add(user);
        }
    }

}
