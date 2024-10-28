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

    public void initDeviceUser() {
        // I'm ignoring the warning, but let's ask about which id we should use
        User deviceUser = User.getInstance(applicationContext);
        String deviceId = Settings.Secure.getString(applicationContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        usersRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String id = task.getResult().getId();
                        Map<String, Object> document = task.getResult().getData();
                        ProfileSystem profile = getProfileSystemFromMap(document);
                        deviceUser.initialize(task.getResult().getId(), profile, (boolean) document.get("isOrganizer"), (boolean) document.get("isAdmin"));
                    } else {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("name", "");
                        userInfo.put("email", "");
                        userInfo.put("address", "");
                        userInfo.put("isOrganizer", false);
                        userInfo.put("isAdmin", false);
                        usersRef.document(deviceId).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    deviceUser.initialize(deviceId, new ProfileSystem(), false, false);
                                } else {
                                    // do some better error handling here. Should probably throw and then we'll
                                    // catch outside this class
                                    Log.d("DB", "Write for User: something went wrong");
                                }
                            }
                        });
                    }
                } else {
                    // do some better error handling here. Should probably throw and then we'll
                    // catch outside this class
                    Log.d("DB", "Query for User: something went wrong");
                }
            }
        });
    }

    public void getUserMap(DbCallback callback) {
        String deviceId = Settings.Secure.getString(applicationContext.getContentResolver(), Settings.Secure.ANDROID_ID);
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

}
