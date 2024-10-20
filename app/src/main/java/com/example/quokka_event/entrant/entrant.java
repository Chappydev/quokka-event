package com.example.quokka_event.entrant;

public class entrant {
    private String deviceID;
    private String profileImage;

    public entrant(String deviceID, String profileImage) {
        this.deviceID = deviceID;
        this.profileImage = profileImage;
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
}
