package com.example.quokka_event.models.event;

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

    private ArrayList<EventManager> participantList;
    private ArrayList<EventManager> waitList;
    private ArrayList<EventManager> cancelledParticipants;


    // Constructor
    public Event(String eventID, String eventName, Date eventDate, String eventLocation, int maxParticipants, int maxWaitlist, ArrayList<EventManager> participantList, Date registrationDeadline, ArrayList<EventManager> waitList, ArrayList<EventManager> cancelledParticipants) {
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

    public ArrayList<EventManager> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<EventManager> participantList) {
        this.participantList = participantList;
    }

    public ArrayList<EventManager> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<EventManager> waitList) {
        this.waitList = waitList;
    }

    public ArrayList<EventManager> getCancelledParticipants() {
        return cancelledParticipants;
    }

    public void setCancelledParticipants(ArrayList<EventManager> cancelledParticipants) {
        this.cancelledParticipants = cancelledParticipants;
    }
}
