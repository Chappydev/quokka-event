package com.example.quokka_event.controllers;

import android.content.Context;

import com.example.quokka_event.models.User;
import com.example.quokka_event.models.admin.ProfileSystem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

// Class to interact with the firebase database to be used for admin's screen
public class DatabaseManager {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference facilityRef;
    private CollectionReference eventsRef;
    private Context applicationContext;
    private static DatabaseManager instance;

    private DatabaseManager(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    // This will always return the same instance anywhere in the code
    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
            instance.initialize();
        }
        return instance;
    }

    // Initialize all attributes and return the current instance
    private DatabaseManager initialize() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");
        facilityRef = db.collection("Facility");
        eventsRef = db.collection("Events");
        return this;
    }
    // delete event from firebase, make sure that it also deletes any data associated with it
    public void deleteEvent(){

    }

    // delete profile from database.
    public void deleteProfile(){

    }

    // Delete hashed qr code data
    public void deleteQRCode(){

    }
    // Delete a facility delete events that are associated it with it
    public void deleteFacility(){

    }

}
