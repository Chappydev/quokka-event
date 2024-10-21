package com.example.quokka_event.entrant;

public class entrant {
    private String deviceID;
    private String profileImage;

    private String entrantEmail;
    private String entrantPhoneNumber;




    // Constructor
    public entrant(String deviceID, String profileImage) {
        this.deviceID = deviceID;
        this.profileImage = profileImage;
        this.entrantEmail = entrantEmail;
        this.entrantPhoneNumber = entrantPhoneNumber;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEntrantEmail() {
        return entrantEmail;
    }

    public void setEntrantEmail(String entrantEmail) {
        this.entrantEmail = entrantEmail;
    }

    public String getEntrantPhoneNumber() {
        return entrantPhoneNumber;
    }

    public void setEntrantPhoneNumber(String entrantPhoneNumber) {
        this.entrantPhoneNumber = entrantPhoneNumber;
    }

    public void scanQRCode(){
        // needs implementation
    }

    public void joinWaitlist(){
        // needs implementation
    }

    public void viewNotifications(){
        // needs implementation
    }

    public void respondToInvite(){
        // needs implementation
    }

    public void cancelWaitlistSpot(){
        // needs implementation
    }
}
