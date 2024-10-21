package com.example.quokka_event.event;

import com.example.quokka_event.entrant.entrant;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class event {

    private String eventID;
    private String eventName;
    private String eventDate;
    private String eventLocation;
    //private Organizer organizer;

    private int maxParticipants;
    private int maxWaitlist;

    private ArrayList<entrant> participantList;
    private ArrayList<entrant> waitList;
    private ArrayList<entrant> cancelledParticipants;


    // Constructor
    public event(String eventID, String eventName, String eventDate, String eventLocation, int maxParticipants, int maxWaitlist, ArrayList<entrant> participantList, ArrayList<entrant> waitList, ArrayList<entrant> cancelledParticipants) {
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

    public ArrayList<entrant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(ArrayList<entrant> participantList) {
        this.participantList = participantList;
    }

    public ArrayList<entrant> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<entrant> waitList) {
        this.waitList = waitList;
    }

    public ArrayList<entrant> getCancelledParticipants() {
        return cancelledParticipants;
    }

    public void setCancelledParticipants(ArrayList<entrant> cancelledParticipants) {
        this.cancelledParticipants = cancelledParticipants;
    }
}
