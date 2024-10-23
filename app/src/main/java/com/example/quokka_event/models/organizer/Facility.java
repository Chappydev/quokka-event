package com.example.quokka_event.models.organizer;

public class Facility {
    private String facilityName;
    private String facilityLocation;

    public Facility(String facilityName, String facilityLocation){
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
    }

    public String getFacilityName() { return facilityName; }
    public String getFacilityLocation() { return facilityLocation; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public void setFacilityLocation(String facilityLocation) { this.facilityName = facilityLocation; }

}
