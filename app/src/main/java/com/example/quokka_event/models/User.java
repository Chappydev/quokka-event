package com.example.quokka_event.models;

import android.content.Context;

// Refactor:
// Could alternatively have a 'Profilelike' interface from which both user and ProfileSystem can draw.
// This would prevent having to get the ProfileSystem from User and then get the Facility from
// ProfileSystem but would increase the upfront boilerplate code in this function.
// I'm opposed to User inheriting from ProfileSystem as I feel like the requirements of these two will
// change more over time. However, both of them inheriting from an abstract class 'Profile' might
// be workable if people prefer that.
/**
 * This class sets up a User object
 */
public class User {
    private final Context applicationContext;
    private static User instance;

    private String deviceID;
    private ProfileSystem profile;
    private boolean organizer;
    private boolean admin;

    // Private constructor means we can only create an instance through the getInstance method
    /**
     * Private constructor
     * @param context
     */
    private User(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    // This will always return the same instance anywhere in the code
    /**
     * Returns the instance of the User class
     * @param context
     * @return the User instance
     */
    public static User getInstance(Context context) {
        if (instance == null) {
            instance = new User(context);
        }
        return instance;
    }

    // Initialize all attributes and return the current instance
    /**
     * Initializes the User instance with the given attributes
     * @param deviceID the device ID
     * @param profile the associated profile
     * @param organizer whether the user is an organizer
     * @param admin whether the user is an admin
     * @return the initialized User instance
     */
    public User initialize(String deviceID, ProfileSystem profile, boolean organizer, boolean admin) {
        this.deviceID = deviceID;
        this.profile = profile;
        this.organizer = organizer;
        this.admin = admin;
        return this;
    }

    /**
     * Gets device ID of user
     * @return device ID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Gets user's profile system
     * @return ProfileSystem associated with the user
     */
    public ProfileSystem getProfile() {
        return profile;
    }

    /**
     * Checks if user is an admin.
     * @return true if user is an admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Sets user's admin status
     * @param admin true for admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Checks if user is an organizer
     * @return true if user is an organizer
     */
    public boolean isOrganizer() {
        return organizer;
    }

    /**
     * Sets user's organizer status
     * @param organizer true for organizer
     */
    public void setOrganizer(boolean organizer) {
        this.organizer = organizer;
    }

    // Shouldn't be able to set this, but I guess we can have a getter if we want *shrug*
    /**
     * Gets application context associated with user
     * @return application context
     */
    public Context getApplicationContext() {
        return applicationContext;
    }
}