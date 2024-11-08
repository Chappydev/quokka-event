package com.example.quokka_event.controllers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.organizer.Facility;
import com.google.android.gms.tasks.Continuation;
import com.example.quokka_event.models.ProfileSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;



// Class to interact with the firebase database to be used for admin's screen
public class DatabaseManager {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference facilityRef;
    private CollectionReference eventsRef;
    private CollectionReference enrollsRef;
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
        enrollsRef = db.collection("Enrolls");
        return this;
    }
    /**
     * interface to send data to other classes.
     */
    public interface RetrieveData {
        /**
         * Retrieve profiles once firebase grabs the documents
         * @param profiles
         */
        void onProfilesLoaded(ArrayList<Map<String, Object>> profiles);
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
                        createProfile(cb, deviceId);
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
     * Adds an event to the db
     * @author speakerchef
     * @param event
     * @param deviceId
     * @param callback
     */
    public void addEvent(Event event, String deviceId, DbCallback callback){
        Map<String, Object> eventPayload = new HashMap<>();
        eventPayload.put("eventId", event.getEventID());
        eventPayload.put("eventName", event.getEventName());
        eventPayload.put("eventDate", event.getEventDate());
        eventPayload.put("eventLocation", event.getEventLocation());
        eventPayload.put("registrationDeadline", event.getRegistrationDeadline());
        eventPayload.put("maxParticipants", event.getMaxParticipants());
        eventPayload.put("maxWaitlist", event.getMaxWaitlist());

        // Pushing the payload to the collection
        eventsRef
                .add(eventPayload)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("eventId", documentReference.getId())
                            .addOnSuccessListener(response -> {
                                callback.onSuccess(documentReference.getId());
                                usersRef.document(deviceId)
                                        .update("isOrganizer", true);
                            })
                            .addOnFailureListener(exception -> callback.onError(exception));
                })
                .addOnFailureListener(exception -> callback.onError(exception));
    }


    /**
     * delete event from firestore, making sure that it also deletes any data associated with it
     * @author speakerchef
     * @param eventId
     * @param callback
     */
    public void deleteEvent(String eventId, DbCallback callback){
        eventsRef
                .document(eventId)
                .delete()
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * @author speakerchef
     * deletes user profile from database.
     * @param deviceId
     * @param callback
     */
    public void deleteProfile(String deviceId, DbCallback callback){
        usersRef
                .document(deviceId)
                .delete()
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Adds a facility profile to firestore
     * @author speakerchef
     * @param facility
     * @param deviceId
     * @param callback
     */
    public void addFacility(Facility facility, String deviceId, DbCallback callback){
        Map<String, Object> payload = new HashMap<>();
        payload.put("facilityName", facility.getFacilityName());
        payload.put("facilityLocation", facility.getFacilityLocation());

        facilityRef
                .add(payload)
                .addOnSuccessListener(DocumentReference -> {
                    DocumentReference.update("facilityId", DocumentReference.getId())
                            .addOnSuccessListener(v -> {
                                callback.onSuccess(DocumentReference.getId());
                                usersRef.document(deviceId)
                                        .update("facilityId", DocumentReference.getId());
                            })
                            .addOnFailureListener(exception -> callback.onError(exception));
                })
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Deletes a facility profile
     * @author speakerchef
     * @param facilityId
     * @param callback
     */
    public void deleteFacility(String facilityId, DbCallback callback){
        facilityRef
                .document(facilityId)
                .delete()
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Gets events associated with a user
     * @author ChappyDev
     * @param userId
     * @param callback
     */
    public void getUserEventList(String userId, DbCallback callback) {
        Log.d("DB", "getUserEventList: " + userId);
        enrollsRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            callback.onSuccess(new ArrayList<Map<String, Object>>());
                        } else {
                            ArrayList<Task<DocumentSnapshot>> taskList = new ArrayList<>();
                            queryDocumentSnapshots.forEach(new Consumer<QueryDocumentSnapshot>() {
                                @Override
                                public void accept(QueryDocumentSnapshot queryDocumentSnapshot) {
                                    Map<String, Object> event = queryDocumentSnapshot.getData();
                                    Task<DocumentSnapshot> task = eventsRef.document((String) event.get("eventId"))
                                            .get()
                                            .addOnFailureListener(e -> Log.e("DB", "grabbing event details for getUserEventList: ", e));
                                    taskList.add(task);
                                }
                            });
                            Tasks.whenAllComplete(taskList)
                                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                                    @Override
                                    public void onSuccess(List<Task<?>> tasks) {
                                        ArrayList<Map<String, Object>> payload = new ArrayList<>();
                                        queryDocumentSnapshots.forEach(new Consumer<QueryDocumentSnapshot>() {
                                            @Override
                                            public void accept(QueryDocumentSnapshot queryDocumentSnapshot) {
                                                Map<String, Object> dataCopy = new HashMap<>(queryDocumentSnapshot.getData());
                                                for (Task task : tasks) {
                                                    if (task.isSuccessful()) {
                                                        Object result = task.getResult();
                                                        if (result instanceof DocumentSnapshot) {
                                                            DocumentSnapshot ds = (DocumentSnapshot) result;
                                                            Map<String, Object> event = ds.getData();
                                                            if (ds.getId().equals(dataCopy.get("eventId"))) {
                                                                dataCopy.put("event", event);
                                                            }
                                                        }
                                                    }
                                                }
                                                payload.add(dataCopy);
                                            }
                                        });
                                        callback.onSuccess(payload);
                                    }
                                });
                        }
                    }
                })
                .addOnFailureListener(callback::onError);
    }





    // Delete hashed qr code data
    public void deleteQRCode(){

    }
    // Delete a facility delete events that are associated it with it
    public void deleteFacility(){

    }

    /**
     * Get all users from firebase database and send it to other classes using RetrieveData
     * interface listener.
     */
    public void getAllProfiles(RetrieveData callback) {
        final ArrayList<Map<String, Object>> usersList = new ArrayList();
        Map<String, Object> userInfo = new HashMap<>();

        usersRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * Grab all user document from users collection in firebase database
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> user = document.getData();
                                user.put("deviceID", document.getId());
                                usersList.add(user);

                            }
                            // Load profiles in ProfileListFragment.java
                            callback.onProfilesLoaded(usersList);
                        }
                        else {
                            Log.d("db", "unable to grab documents from firebase");
                        }
                    }

                });
    }

    /**
     * Update profile information
     * @author speakerchef
     * @param deviceId
     * @param name
     * @param email
     * @param phone
     * @param callback
     */

    public void updateProfile(String deviceId, String name, String email, String phone, Boolean notificationPreference, DbCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("notifications", notificationPreference);

        usersRef.document(deviceId)
                .update(updates)
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }


    /**
     * Update facility profile information
     * @author speakerchef
     * @param facilityId
     * @param name
     * @param address
     * @param callback
     */
    public void updateFacility(String facilityId, String name, String address, DbCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("facilityName", name);
        updates.put("facilityLocation", address);

        facilityRef.document(facilityId)
                .update(updates)
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }


    /**
     * Queries a single facility
     * @author speakerchef
     * @param facilityId
     * @param callback
     */
    public void getFacility(String facilityId, DbCallback callback) {
        facilityRef.document(facilityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onError(new Exception("Facility not found"));
                    }
                })
                .addOnFailureListener(exception -> callback.onError(exception));
    }
}
