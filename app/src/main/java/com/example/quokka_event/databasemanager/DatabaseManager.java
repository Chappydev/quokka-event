package com.example.quokka_event.databasemanager;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileInputStream;
// Class to interact with the firebase database to be used for admin's screen
public class DatabaseManager {
    private FirebaseFirestore database;
    private CollectionReference ref;

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
