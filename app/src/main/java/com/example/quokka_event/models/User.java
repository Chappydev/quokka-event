package com.example.quokka_event.models;

import android.content.Context;

import com.example.quokka_event.models.admin.ProfileSystem;

// Could alternatively have a 'Profilelike' interface from which both user and ProfileSystem can draw.
// This would prevent having to get the ProfileSystem from User and then get the Facility from
// ProfileSystem but would increase the upfront boilerplate code in this function.
// I'm opposed to User inheriting from ProfileSystem as I feel like the requirements of these two will
// change more over time. However, both of them inheriting from an abstract class 'Profile' might
// be workable if people prefer that.
public class User {
    private final Context applicationContext;
    private static User instance;

    private String deviceID;
    private ProfileSystem profile;
    private boolean organizer;
    private boolean admin;

    // Private constructor means we can only create an instance through the getInstance method
    private User(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    // This will always return the same instance anywhere in the code
    public static User getInstance(Context context) {
        if (instance == null) {
            instance = new User(context);
        }
        return instance;
    }

    // Initialize all attributes and return the current instance
    public User initialize(String deviceID, ProfileSystem profile, boolean organizer, boolean admin) {
        this.deviceID = deviceID;
        this.profile = profile;
        this.organizer = organizer;
        this.admin = admin;
        return this;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public ProfileSystem getProfile() {
        return profile;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isOrganizer() {
        return organizer;
    }

    public void setOrganizer(boolean organizer) {
        this.organizer = organizer;
    }

    // Shouldn't be able to set this, but I guess we can have a getter if we want *shrug*
    public Context getApplicationContext() {
        return applicationContext;
    }
}
