package com.example.quokka_event.models;

public class Notification {
    public String notifTitle;
    public String notifMessage;
    private String eventId;
    private String eventName;
    private String notifId;

    /**
     * Constructor method for a notification object.
     * @author mylayambao
     * @param notifId
     * @param notifTitle
     * @param notifMessage
     * @param eventId
     * @param eventName
     */
    public Notification(String notifId, String notifTitle, String notifMessage, String eventId, String eventName){
        this.notifTitle = notifTitle;
        this.notifMessage = notifMessage;
        this.eventId = eventId;
        this.eventName = eventName;
    }

    /**
     * Default constructor
     * @author mylayambao
     */
    public Notification(){
        // leave empty
    }


    /**
     * Gets the notification title
     * @return notifTitle
     */
    public String getNotifTitle() {return notifTitle;}

    /**
     * Sets the notification title.
     * @param notifTitle
     */
    public void setNotifTitle(String notifTitle) {this.notifTitle = notifTitle;}

    /**
     * Gets the notification message.
     * @return notifMessage
     */
    public String getNotifMessage() {return notifMessage;}

    /**
     * Sets the notification message.
     * @param notifMessage
     */
    public void setNotifMessage(String notifMessage) {this.notifMessage = notifMessage;}

    /**
     * Gets the eventId.
     * @return eventId
     */
    public String getEventId() {return eventId;}

    /**
     * Sets the eventId.
     * @param eventId
     */
    public void setEventId(String eventId) {this.eventId = eventId;}

    /**
     * Gets the event name.
     * @return
     */
    public String getEventName() {return eventName;}

    /**
     * Sets the eventName.
     * @param eventName
     */
    public void setEventName(String eventName) {this.eventName = eventName;}

    /**
     * Gets the notificationId.
     * @return
     */
    public String getNotifId() {return notifId;}

    /**
     * Sets the notificationId.
     * @param notifId
     */
    public void setNotifId(String notifId) {this.notifId = notifId;}
}


