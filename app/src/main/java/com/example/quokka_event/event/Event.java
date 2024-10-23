package com.example.quokka_event.event;

import com.example.quokka_event.entrant.Entrant;

import java.util.ArrayList;

public class Event {

    private String eventID;
    private String eventName;
    private String eventDate;
    private String eventLocation;
    //private Organizer organizer;

    private int maxParticipants;
    private int maxWaitlist;

    private ArrayList<Entrant> participantList;
    private ArrayList<Entrant> waitList;
    private ArrayList<Entrant> cancelledParticipants;


    // Constructor
    public Event(String eventID, String eventName, String eventDate, String eventLocation, int maxParticipants, int maxWaitlist, ArrayList<Entrant> participantList, ArrayList<Entrant> waitList, ArrayList<Entrant> cancelledParticipants) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDate = eventDate;
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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
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

    public ArrayList<Entrant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<Entrant> participantList) {
        this.participantList = participantList;
    }

    public ArrayList<Entrant> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<Entrant> waitList) {
        this.waitList = waitList;
    }

    public ArrayList<Entrant> getCancelledParticipants() {
        return cancelledParticipants;
    }

    public void setCancelledParticipants(ArrayList<Entrant> cancelledParticipants) {
        this.cancelledParticipants = cancelledParticipants;
    }
}
