package com.example.quokka_event.models.organizer;

/**
 * This class sets up a Facility object
 */
public class Facility {
    private String facilityName;
    private String facilityLocation;
    private String facilityId;

//    public Facility(String facilityName, String facilityLocation, String facilityId) {
//        this.facilityName = facilityName;
//        this.facilityLocation = facilityLocation;
//        this.facilityId = facilityId;
//    }

    /**
     * Constructor for the Facility class
     */
    public Facility() {
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
