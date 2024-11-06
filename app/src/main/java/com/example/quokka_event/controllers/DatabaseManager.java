package com.example.quokka_event.controllers;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.admin.ProfileSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

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

    // vvvv This function should be split into 2. The create user part should be its own function
    // I made a split this function for better readability see createProfile()
    /**
     * This function will either find the user or if it doesn't exist yet, it will create a new user
     * with the deviceId.
     * @author Chappydev
     * @param cb An instance of the db callback interface.
     * @param deviceId The String deviceId value of the current device.
     * @see DbCallback
     */
    public void getDeviceUser(DbCallback cb, String deviceId) {
        // I'm ignoring the warning, but let's ask about which id we should use
        usersRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Successfully found the user based on the device's id:
                    if (task.getResult().exists()) {
                        Map<String, Object> document = task.getResult().getData();
                        ProfileSystem profile = getProfileSystemFromMap(document);
                        String id = task.getResult().getId();

                        // Set up the data and pass it into the callback
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("deviceID", id);
                        userData.put("profile", profile);
                        userData.put("organizer", document.get("isOrganizer"));
                        userData.put("admin", document.get("isAdmin"));
                        cb.onSuccess(userData);

                    // Successful search but no such user
                    } else {
                        createProfile(cb,deviceId);
                    }

                // There was an error when trying to get the user data:
                } else {
                    Log.e("DB", "Query for User: something went wrong", task.getException());
                    // Can handle this error in the UI through the onError callback
                    cb.onError(task.getException());
                }
            }
        });
    }

    /**
     * This function will find a single user based on their deviceID.
     * @author Chappydev
     * @param callback An instance of the db callback interface.
     * @param deviceId The deviceID of the user to find
     * @see DbCallback
     */
    public void getUserData(DbCallback callback, String deviceId) {
        usersRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("DB", "getUserMap callback: " + task.getResult().getData().toString());
                    callback.onSuccess(task.getResult().getData());
                } else {
                    Log.e("DB", "getUserMap callback", task.getException());
                    callback.onError(task.getException());
                }
            }
        });
    }

    private ProfileSystem getProfileSystemFromMap(Map<String, Object> map) {
        ProfileSystem profile = new ProfileSystem();
        profile.setName((String) map.getOrDefault("name", ""));
        profile.setAddress((String) map.getOrDefault("address", ""));
        profile.setEmail((String) map.getOrDefault("email", ""));
        return profile;
    }

    /**
     * Creates a new profile in the firestore database
     * @author Chappydev
     * @param cb
     * @param deviceId
     */
    public void createProfile(DbCallback cb, String deviceId){
        // So we will create the user instead:
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", "");
        userInfo.put("email", "");
        userInfo.put("address", "");
        userInfo.put("isOrganizer", false);
        userInfo.put("isAdmin", false);
        usersRef.document(deviceId).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Successfully created a user with default values
                if (task.isSuccessful()) {
                    // Set up the data and pass it into the callback
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("deviceID", deviceId);
                    userData.put("profile", new ProfileSystem());
                    userData.put("organizer", false);
                    userData.put("admin", false);
                    cb.onSuccess(userData);

                    // There was an error trying to create the new user
                } else {
                    Log.e("DB", "Write for User: something went wrong creating the new user", task.getException());
                    // Can handle this error in the UI through the onError callback
                    cb.onError(task.getException());
                }
            }
        });
    }

    /**
     * Updates a profile in the database
     * @author saimonnk
     * @param profile
     */
    public void updateProfile(ProfileSystem profile){
        String deviceId = profile.getDeviceID();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", profile.getName());
        userInfo.put("email", profile.getEmail());
        userInfo.put("address", profile.getAddress());
        userInfo.put("phonenumber", profile.getPhoneNumber());
        usersRef.document(deviceId).update(userInfo);
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

    /**
     * This function will either find the user or if it doesn't exist yet, it will create a new user
     * with the deviceId.
     * @author Chappydev
     * @param cb An instance of the db callback interface.
     * @param deviceId The String deviceId value of the current device.
     * @see DbCallback
     */
    public void getDeviceUser(DbCallback cb, String deviceId) {
        // I'm ignoring the warning, but let's ask about which id we should use
        usersRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Successfully found the user based on the device's id:
                    if (task.getResult().exists()) {
                        Map<String, Object> document = task.getResult().getData();
                        ProfileSystem profile = getProfileSystemFromMap(document);
                        String id = task.getResult().getId();

                        // Set up the data and pass it into the callback
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("deviceID", id);
                        userData.put("profile", profile);
                        userData.put("organizer", document.get("isOrganizer"));
                        userData.put("admin", document.get("isAdmin"));
                        cb.onSuccess(userData);

                    // Successful search but no such user
                    } else {
                        // So we will create the user instead:
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("name", "");
                        userInfo.put("email", "");
                        userInfo.put("address", "");
                        userInfo.put("isOrganizer", false);
                        userInfo.put("isAdmin", false);
                        usersRef.document(deviceId).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Successfully created a user with default values
                                if (task.isSuccessful()) {
                                    // Set up the data and pass it into the callback
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("deviceID", deviceId);
                                    userData.put("profile", new ProfileSystem());
                                    userData.put("organizer", false);
                                    userData.put("admin", false);
                                    cb.onSuccess(userData);

                                // There was an error trying to create the new user
                                } else {
                                    Log.e("DB", "Write for User: something went wrong creating the new user", task.getException());
                                    // Can handle this error in the UI through the onError callback
                                    cb.onError(task.getException());
                                }
                            }
                        });
                    }

                // There was an error when trying to get the user data:
                } else {
                    Log.e("DB", "Query for User: something went wrong", task.getException());
                    // Can handle this error in the UI through the onError callback
                    cb.onError(task.getException());
                }
            }
        });
    }

    /**
     * This function will find a single user based on their deviceID.
     * @author Chappydev
     * @param callback An instance of the db callback interface.
     * @param deviceId The deviceID of the user to find
     * @see DbCallback
     */
    public void getUserData(DbCallback callback, String deviceId) {
        usersRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("DB", "getUserMap callback: " + task.getResult().getData().toString());
                    callback.onSuccess(task.getResult().getData());
                } else {
                    Log.e("DB", "getUserMap callback", task.getException());
                    callback.onError(task.getException());
                }
            }
        });
    }

    private ProfileSystem getProfileSystemFromMap(Map<String, Object> map) {
        ProfileSystem profile = new ProfileSystem();
        profile.setName((String) map.getOrDefault("name", ""));
        profile.setAddress((String) map.getOrDefault("address", ""));
        profile.setEmail((String) map.getOrDefault("email", ""));
        return profile;
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

    /**
     * Get all users from firebase database.
     */
    public void getAllProfiles(){
        ArrayList<ProfileSystem> users = new ArrayList<ProfileSystem>();
        usersRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                ProfileSystem user = new ProfileSystem();


                            }
                        } else{

                        }
                    }
                });
    }
}
