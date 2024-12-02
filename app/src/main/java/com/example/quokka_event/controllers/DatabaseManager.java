package com.example.quokka_event.controllers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.Notification;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.organizer.Facility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class to interact with the firebase database to be used for admin's screen.
 */
public class DatabaseManager {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference facilityRef;
    private CollectionReference eventsRef;
    private CollectionReference enrollsRef;
    private CollectionReference notificationsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
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
        notificationsRef = db.collection("Notifications");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        return this;
    }

    /**
     * interface to send data to other classes.
     */
    public interface RetrieveData {
        /**
         * Retrieve profiles once firebase grabs the documents
         *
         * @param list
         */
        void onDataLoaded(ArrayList<Map<String, Object>> list);
    }

    /**
     * This function will either find the user or if it doesn't exist yet, it will create a new user
     * with the deviceId.
     *
     * @param cb       An instance of the db callback interface.
     * @param deviceId The String deviceId value of the current device.
     * @author Chappydev
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
     *
     * @param cb
     * @param deviceId
     * @author Chappydev and Soaiba
     */
    public void createProfile(DbCallback cb, String deviceId) {
        // So we will create the user instead:
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("deviceId", deviceId);
        userInfo.put("name", "");
        userInfo.put("email", "");
        userInfo.put("address", "");
        userInfo.put("isOrganizer", false);
        userInfo.put("isAdmin", false);
        userInfo.put("profileImage", 0);

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
     *
     * @param callback An instance of the db callback interface.
     * @param deviceId The deviceID of the user to find
     * @author Chappydev
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

    /**
     * Converts map of profile data into a ProfileSystem object
     *
     * @param map
     * @return ProfileSystem object with the profile data
     */
    private ProfileSystem getProfileSystemFromMap(Map<String, Object> map) {
        ProfileSystem profile = new ProfileSystem();
        profile.setName((String) map.getOrDefault("name", ""));
        profile.setAddress((String) map.getOrDefault("address", ""));
        profile.setEmail((String) map.getOrDefault("email", ""));
        if (map.get("profileImagePath") != null) {
            Log.d("DB", "getProfileSystemFromMap: " + map.get("profileImagePath"));
            StorageReference ref = storageRef.child((String) map.get("profileImagePath"));
            profile.setProfileImageRef(ref);
        }
        return profile;
    }

    /**
     * Adds an event to the db
     *
     * @param event
     * @param deviceId
     * @param callback
     * @author speakerchef
     */
    public void addEvent(Event event, String deviceId, DbCallback callback) {
        Map<String, Object> eventPayload = new HashMap<>();
        eventPayload.put("eventId", event.getEventID());
        eventPayload.put("eventName", event.getEventName());
        eventPayload.put("eventDate", event.getEventDate());
        eventPayload.put("eventLocation", event.getEventLocation());
        eventPayload.put("registrationDeadline", event.getRegistrationDeadline());
        eventPayload.put("maxParticipants", event.getMaxParticipants());
        eventPayload.put("maxWaitlist", event.getMaxWaitlist());
        eventPayload.put("description", event.getDescription());
        eventPayload.put("geolocationEnabled", event.getGeolocationEnabled());

        // Pushing the payload to the collection
        usersRef
                .document(deviceId)
                .get()
                .addOnSuccessListener(userSnapShot -> {
                    if (!userSnapShot.exists()) {
                        callback.onError(new Exception("ERROR: User not found"));
                    }
                    Map<String, Object> userMap = userSnapShot.getData();
                    if (userMap.get("facilityId") == null) {
                        callback.onError(new Exception("ERROR: No facility owned. Please create a facility first!"));
                    } else {
                        eventPayload.put("facilityId", userSnapShot.get("facilityId"));
                        eventsRef
                                .add(eventPayload)
                                .addOnSuccessListener(DocumentReference -> {
                                    DocumentReference.update("eventId", DocumentReference.getId())
                                            .addOnSuccessListener(response -> {
                                                ArrayList<Map<String, Object>> currentEvents = (ArrayList<Map<String, Object>>) userMap.get("events");
                                                if (currentEvents == null) {
                                                    currentEvents = new ArrayList<>();
                                                }
                                                eventPayload.put("eventId", DocumentReference.getId());
                                                currentEvents.add(eventPayload);
                                                userSnapShot.getReference().update("events", currentEvents)
                                                        .addOnSuccessListener(v -> callback.onSuccess(DocumentReference.getId()))
                                                        .addOnFailureListener(e -> callback.onError(e));
                                            })
                                            .addOnFailureListener(exception -> callback.onError(exception));
                                })
                                .addOnFailureListener(exception -> callback.onError(exception));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));

    }

    /**
     * delete event from firestore, making sure that it also deletes any data associated with it
     *
     * @param eventId
     * @param callback
     * @author speakerchef
     */
    public void deleteEvent(String eventId, DbCallback callback) {
        ArrayList<Task<Void>> deleteTasks = new ArrayList<>();
        eventsRef.document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Map<String, Object> event = documentSnapshot.getData();
                if (event != null){
                    usersRef
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    for (DocumentSnapshot doc : documentSnapshots){
                                        ArrayList<Map<String, Object>> events = new ArrayList<>();
                                        if (events != null) {
                                            // Match and remove the event by eventId
                                            String eventIdToRemove = (String) event.get("eventId");
                                            events.removeIf(e -> eventIdToRemove.equals(e.get("eventId")));
                                            Task<Void> task = doc.getReference().update("events", events);
                                            deleteTasks.add(task);
                                        } else {
                                            Log.e("DeleteEvent", "No events array in user document: " + doc.getId());
                                        }
                                    }
                                }
                            });
                }
            }
        });
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot doc : documentSnapshots){
                            Task<Void> task = doc.getReference().delete();
                            deleteTasks.add(task);
                        }
                    }
                });
        eventsRef
                .document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Task<Void> task = documentSnapshot.getReference().delete();
                        deleteTasks.add(task);
                        Tasks.whenAllComplete()
                                .addOnSuccessListener(response -> callback.onSuccess(response))
                                .addOnFailureListener(exception -> callback.onError(exception));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));

    }

    /**
     * @param deviceId
     * @param callback
     * @author speakerchef
     * deletes user profile from database.
     */
    public void deleteProfile(String deviceId, DbCallback callback) {
        usersRef
                .document(deviceId)
                .delete()
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Adds a facility profile to firestore
     *
     * @param facility
     * @param deviceId
     * @param callback
     * @author speakerchef
     */
    public void addFacility(Facility facility, String deviceId, DbCallback callback) {
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
     *
     * @param facilityId
     * @param callback
     * @author speakerchef
     */
    public void deleteFacility(String facilityId, DbCallback callback) {
        ArrayList<Task<Void>> deleteTasks = new ArrayList<>();
        usersRef
                .whereEqualTo("facilityId", facilityId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot doc : documentSnapshots){
                            Task<Void> task = doc.getReference().update("events", FieldValue.delete());
                            Task<Void> deltask = doc.getReference().update("facilityId", FieldValue.delete());
                            deleteTasks.add(task);
                            deleteTasks.add(deltask);
                        }
                    }
                });
        eventsRef
                .whereEqualTo("facilityId", facilityId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot doc : documentSnapshots){
                            enrollsRef.whereEqualTo("eventId", (String) doc.get("eventId")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    for (DocumentSnapshot doc : documentSnapshots){
                                        Task<Void> delTask = doc.getReference().delete();
                                        deleteTasks.add(delTask);
                                    }
                                }
                            }).addOnFailureListener(e -> callback.onError(e));
                            Task<Void> task = doc.getReference().delete();
                            deleteTasks.add(task);

                        }
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));

        facilityRef
                .document(facilityId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Task<Void> deleteFacility = documentSnapshot.getReference().delete();
                        deleteTasks.add(deleteFacility);
                        Tasks.whenAllComplete()
                                .addOnSuccessListener(response -> callback.onSuccess(response))
                                .addOnFailureListener(exception -> callback.onError(exception));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));

    }

    /**
     * Gets events associated with a user
     *
     * @param userId
     * @param callback
     * @author ChappyDev
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
                                    Task<DocumentSnapshot> task = eventsRef.document(queryDocumentSnapshot.getId())
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
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("DB", "getting events from the enrolls data onFailure: ", e);
                                            callback.onError(e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Gets list of all events.
     *
     * @param callback
     * @author speakerchef
     */
    public void getEventList(DbCallback callback) {
        eventsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> eventList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> eventData = document.getData();
                        eventList.add(eventData);
                    }
                    callback.onSuccess(eventList);
                })
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Gets a single event from the database with its ID
     *
     * @param eventId
     * @param callback
     */
    public void getSingleEvent(String eventId, DbCallback callback) {
        eventsRef
                .document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> eventWithId = documentSnapshot.getData();
                            eventWithId.put("id", documentSnapshot.getId());
                            callback.onSuccess(eventWithId);
                        } else {
                            callback.onError(new Exception("No event found"));
                        }

                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    /**
     * This method gets events by their ID.
     *
     * @param callback
     * @author Soaiba
     */
    public void getEventsByIds(List<String> eventIds, DbCallback callback) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (int i = 0; i < eventIds.size(); i++) {
            String eventId = eventIds.get(i);
            Task<DocumentSnapshot> task = eventsRef.document(eventId).get();
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(completedTasks -> {
                    List<Map<String, Object>> eventDataList = new ArrayList<>();
                    for (int i = 0; i < completedTasks.size(); i++) {
                        Task<?> task = completedTasks.get(i);
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                            if (document.exists()) {
                                eventDataList.add(document.getData());
                            }
                        }
                    }
                    callback.onSuccess(eventDataList);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Get all users from firebase database and send it to other classes using RetrieveData
     * interface listener.
     */
    public void getAllProfiles(RetrieveData callback) {
        final ArrayList<Map<String, Object>> usersList = new ArrayList();

        usersRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * Grab all user document from users collection in firebase database
                     *
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
                            callback.onDataLoaded(usersList);
                        } else {
                            Log.d("db", "unable to grab documents from firebase");
                        }
                    }
                });
    }

    /**
     * This method updates status of event.
     *
     * @param eventId   id of event we are updating.
     * @param userId    id of user whose event we are updating.
     * @param newStatus updated status.
     * @param callback
     * @author Soaiba
     */
    public void updateEventStatus(String eventId, String userId, String newStatus, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference()
                                .update("status", newStatus)
                                .addOnSuccessListener(response -> {
                                    callback.onSuccess(response);
                                })
                                .addOnFailureListener(exception -> {
                                    callback.onError(exception);
                                });
                    } else {
                        callback.onError(new Exception("Enrollment not found"));
                    }
                })
                .addOnFailureListener(exception -> {
                    callback.onError(exception);
                });
    }

    /**
     * Adds user to an event waitlist
     *
     * @param eventId
     * @param userId
     * @param callback
     * @author speakerchef
     */
    public void joinWaitlist(String eventId, String userId, DbCallback callback) {
        Map<String, Object> enrollData = new HashMap<>();
        enrollData.put("eventId", eventId);
        enrollData.put("userId", userId);
        enrollData.put("status", "Waiting");

        enrollsRef.add(enrollData)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(e -> callback.onError(e));
    }

    /**
     * Gets all facilities from database
     *
     * @param callback
     */
    public void getAllFacilities(RetrieveData callback) {
        final ArrayList<Map<String, Object>> facilityList = new ArrayList();
        facilityRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> facility = document.getData();
                        facilityList.add(facility);
                    }
                    callback.onDataLoaded(facilityList);
                }
            }
        });
    }

    /**
     * Grab all events from database
     *
     * @param callback
     */
    public void getAllEvents(RetrieveData callback) {
        final ArrayList<Map<String, Object>> eventList = new ArrayList();
        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            /**
             * Get events from database. Call after it is complete.
             * @param task
             */
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> event = document.getData();
                        eventList.add(event);
                    }
                    callback.onDataLoaded(eventList);
                }
            }
        });
    }

    /**
     * Update profile information
     *
     * @param deviceId
     * @param updates
     * @param callback
     * @author speakerchef
     */
    public void updateProfile(String deviceId, Map<String, Object> updates, DbCallback callback) {
        usersRef.document(deviceId)
                .update(updates)
                .addOnSuccessListener(response -> callback.onSuccess(response))
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Update facility profile information
     *
     * @param facilityId
     * @param name
     * @param address
     * @param callback
     * @author speakerchef
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
     *
     * @param facilityId
     * @param callback
     * @author speakerchef
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

    /**
     * Get all events by organizer
     * @param deviceId
     * @param callback
     */
    public void getOrganizerEvents(String deviceId, DbCallback callback) {
        usersRef
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onError(new Exception("ERROR: User not found"));
                    } else {
                        ArrayList<Map<String, Object>> currentEvents = (ArrayList<Map<String, Object>>) documentSnapshot.get("events");
                        if (currentEvents == null) {
                            currentEvents = new ArrayList<>();
                        }
                        callback.onSuccess(currentEvents);
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }


    /**
     * updates event details
     *
     * @param eventId
     * @param updates
     * @param callback
     * @author speakerchef
     */
    public void updateEvent(String eventId, String userId, Map<String, Object> updates, DbCallback callback) {
        usersRef.document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    ArrayList<Map<String, Object>> organizerEvents =
                            (ArrayList<Map<String, Object>>) documentSnapshot.get("events");

                    if (organizerEvents != null) {
                        // Update event in organizer's array
                        for (Map<String, Object> event : organizerEvents) {
                            if (eventId.equals(event.get("eventId"))) {
                                event.putAll(updates);
                                break;
                            }
                        }

                        // Update both database locations
                        Task<Void> eventUpdate = eventsRef.document(eventId).update(updates);
                        Task<Void> userUpdate = usersRef.document(userId).update("events", organizerEvents);

                        Tasks.whenAllComplete(eventUpdate, userUpdate)
                                .addOnSuccessListener(tasks -> callback.onSuccess(null))
                                .addOnFailureListener(callback::onError);
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Gets the list of all the users on the waitlist of an event.
     *
     * @param eventId
     * @param callback
     * @author speakerchef
     */
    public void getWaitlistEntrants(String eventId, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Waiting")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Map<String, Object>> waitlistEntrants = new ArrayList<>();
                    ArrayList<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getString("userId");
                        if (userId != null) {
                            userTasks.add(usersRef.document(userId).get());
                        }
                    }

                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(tasks -> {
                                for (Task<?> task : tasks) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userDoc = (DocumentSnapshot) task.getResult();
                                        if (userDoc.exists()) {
                                            waitlistEntrants.add(userDoc.getData());
                                        }
                                    }
                                }
                                callback.onSuccess(waitlistEntrants);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Gets the list of all users attending an event.
     *
     * @param eventId
     * @param callback
     * @author speakerchef (edited by mylayambao)
     */
    public void getAttendingEntrants(String eventId, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Accepted")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Map<String, Object>> attendingEntrants = new ArrayList<>();
                    ArrayList<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getString("userId");
                        if (userId != null) {
                            userTasks.add(usersRef.document(userId).get());
                        }
                    }

                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(tasks -> {
                                for (Task<?> task : tasks) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userDoc = (DocumentSnapshot) task.getResult();
                                        if (userDoc.exists()) {
                                            attendingEntrants.add(userDoc.getData());
                                        }
                                    }
                                }
                                callback.onSuccess(attendingEntrants);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Gets the list of all users invited to an event.
     *
     * @param eventId
     * @param callback
     * @author speakerchef (edited by mylayambao)
     */
    public void getInvitedEntrants(String eventId, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Invited")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Map<String, Object>> invitedEntrants = new ArrayList<>();
                    ArrayList<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getString("userId");
                        if (userId != null) {
                            userTasks.add(usersRef.document(userId).get());
                        }
                    }

                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(tasks -> {
                                for (Task<?> task : tasks) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userDoc = (DocumentSnapshot) task.getResult();
                                        if (userDoc.exists()) {
                                            invitedEntrants.add(userDoc.getData());
                                        }
                                    }
                                }
                                callback.onSuccess(invitedEntrants);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Gets the list of all cancelled entrants for an event
     *
     * @param eventId
     * @param callback
     * @author speakerchef (edited by mylayambao)
     */
    public void getCancelledEntrants(String eventId, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "Declined")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Map<String, Object>> cancelledEntrants = new ArrayList<>();
                    ArrayList<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getString("userId");
                        if (userId != null) {
                            userTasks.add(usersRef.document(userId).get());
                        }
                    }

                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(tasks -> {
                                for (Task<?> task : tasks) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userDoc = (DocumentSnapshot) task.getResult();
                                        if (userDoc.exists()) {
                                            cancelledEntrants.add(userDoc.getData());
                                        }
                                    }
                                }
                                callback.onSuccess(cancelledEntrants);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);

    }


    /**
     * Gets the list of all entrants for an event
     * @author speakerchef (edited by mylayambao)
     * @param eventId
     * @param callback
     */
    public void getAllEntrants(String eventId, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Map<String, Object>> cancelledEntrants = new ArrayList<>();
                    ArrayList<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String userId = doc.getString("userId");
                        if (userId != null) {
                            userTasks.add(usersRef.document(userId).get());
                        }
                    }

                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(tasks -> {
                                for (Task<?> task : tasks) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userDoc = (DocumentSnapshot) task.getResult();
                                        if (userDoc.exists()) {
                                            cancelledEntrants.add(userDoc.getData());
                                        }
                                    }
                                }
                                callback.onSuccess(cancelledEntrants);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }



    /**
     * Creates a notification object and stores it in the database.
     * @author mylayambao
     * @param notification
     * *@param deviceId
     * @param callback
     */
    public void addNotification(Notification notification, DbCallback callback){
        Map<String, Object> notificationMap = new HashMap<>();
        //notificationMap.put("eventId", notification.getEventId());
        notificationMap.put("notifMessage", notification.getNotifMessage());
        notificationMap.put("notifTitle", notification.getNotifTitle());
        notificationMap.put("eventId", notification.getEventId());
        notificationMap.put("recipients", notification.getRecipients());

        notificationsRef
                .add(notificationMap)
                .addOnSuccessListener(DocumentReference -> {
                    DocumentReference.update("notificationId", DocumentReference.getId())
                            .addOnSuccessListener(v -> {
                                callback.onSuccess("Notification added with ID: " + DocumentReference.getId());
                            })
                            .addOnFailureListener(exception -> callback.onError(exception));
                })
                .addOnFailureListener(exception -> callback.onError(exception));
    }

    /**
     * Add image path to the user's document
     * @author Chappydev
     * @param deviceId User id
     * @param path path to add to user
     * @param callback callback functions
     */
    public void addImageToUser(String deviceId, String path, DbCallback callback) {
        usersRef.document(deviceId).update("profileImagePath", path)
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * Log error and call callback
                     * @param e
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DB", "addImageToUser onFailure: ", e);
                        callback.onError(e);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * Log success and return some basic info to callback
                     * @param unused
                     */
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DB", "addImageToUser succeeded!");
                        Map<String, Object> resultInfo = new HashMap<>();
                        resultInfo.put("success", true);
                        resultInfo.put("deviceId", deviceId);
                        resultInfo.put("path", path);
                        callback.onSuccess(resultInfo);
                    }
                });
    }

    /**
     * Adds an image (poster) to an event.
     * @author mylayambao & Chappydev
     * @param eventId event id
     * @param path path to the image
     * @param callback db callback
     * @since project part 4
     */
    public void addImageToEvent(String eventId, String path, DbCallback callback) {
        eventsRef.document(eventId).update("posterImagePath", path)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DB", "addImageToEvent onFailure: ", e);
                        callback.onError(e);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DB", "addImageEvent succeeded!");
                        Map<String, Object> resultInfo = new HashMap<>();
                        resultInfo.put("success", true);
                        resultInfo.put("eventId", eventId);
                        resultInfo.put("path", path);
                        callback.onSuccess(resultInfo);
                    }
                });
    }

    /**
     * Deletes an event poster from the database.
     * @author mylayambao
     * @param eventId event id
     * @param callback db callback
     * @since project part 4
     */
    public void deleteEventPoster(String eventId, DbCallback callback){
        db.collection("Events").document(eventId)
                .update("posterImagePath", FieldValue.delete())
                .addOnSuccessListener(response -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    /**
     * Delete profile picture from Firebase Storage and the path from the Users collection
     * @author Chappydev
     * @param deviceId id of user
     * @param pictureRef reference to the location of the image
     * @param callback callback functions
     */
    public void deleteProfilePicture(String deviceId, StorageReference pictureRef, DbCallback callback) {
        pictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    /**
                     * When successful, also remove the path from the user document
                     * @param unused
                     */
                    @Override
                    public void onSuccess(Void unused) {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("")
                        usersRef.document(deviceId)
                                .update("profileImagePath", FieldValue.delete())
                                .addOnFailureListener(new OnFailureListener() {
                                    /**
                                     * call onError callback
                                     * @param e
                                     */
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.onError(e);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    /**
                                     * call onSuccess callback
                                     * @param unused
                                     */
                                    @Override
                                    public void onSuccess(Void unused) {
                                        callback.onSuccess("Successfully deleted");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * call onError callback
                     * @param e
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError(e);
                    }
                });
    }

    public void getEventByQRHash(String qrHash, DbCallback callback){
        eventsRef
                .whereEqualTo("qrHash", qrHash)
                .get()
                .addOnSuccessListener(querySnapshot-> {
                    if (!querySnapshot.isEmpty()){
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        Map<String, Object> data = documentSnapshot.getData();
                        data.put("eventId", documentSnapshot.getId());
                        callback.onSuccess(data);
                    } else {
                        callback.onError(new Exception("No event found"));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    /**
     * Returns all entrants for an event with their status.
     * @author speakerchef
     * @param eventId
     * @param callback
     */
    public void getEnrolls(String eventId, DbCallback callback) {
        enrollsRef
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<Map<String, Object>> enrolledUsers = new ArrayList<>();
                    ArrayList<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    ArrayList<DocumentSnapshot> enrollDocs = new ArrayList<>(querySnapshot.getDocuments());

                    for (DocumentSnapshot enrollDoc : enrollDocs) {
                        String userId = enrollDoc.getString("userId");
                        if (userId != null) {
                            userTasks.add(usersRef.document(userId).get());
                        }
                    }

                    Tasks.whenAllComplete(userTasks)
                            .addOnSuccessListener(tasks -> {
                                for (int i = 0; i < tasks.size(); i++) {
                                    Task<?> task = tasks.get(i);
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot userDoc = (DocumentSnapshot) task.getResult();
                                        if (userDoc.exists()) {
                                            Map<String, Object> userData = userDoc.getData();
                                            if (userData != null) {
                                                // add enrollment status to user data
                                                userData.put("status", enrollDocs.get(i).getString("status"));
                                                enrolledUsers.add(userData);
                                            }
                                        }
                                    }
                                }
                                callback.onSuccess(enrolledUsers);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }


    public void inviteUsers(String eventId, ArrayList<Map<String, Object>> waitList, DbCallback callback){
        WriteBatch batch = db.batch();
        Map<String, Object> invite = new HashMap<>();
        invite.put("status", "Invited");
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (Map<String, Object>enroll : waitList){
            String userId = (String) enroll.get("deviceId");
            Task<QuerySnapshot> task = enrollsRef
                    .whereEqualTo("eventId", eventId)
                    .whereEqualTo("status", "Waiting")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()){
                            batch.update(doc.getReference(), "status", "Invited");
                        }
                    })
                    .addOnFailureListener(callback::onError);
            tasks.add(task);
        }
        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(success -> {
                    batch.commit()
                            .addOnSuccessListener(s -> {
                                callback.onSuccess(waitList);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * This method gets notifications for user based on deviceId.
     * @param deviceId the deviceId of the user
     * @param callback
     * @author Soaiba
     */
    public void getUserNotifications(String deviceId, DbCallback callback) {
        Log.d("Database", "Querying notifications for deviceId: " + deviceId);

//        notificationsRef.get().addOnSuccessListener(querySnapshot -> {
//            Log.d("Firestore", "Total notifications: " + querySnapshot.size());
//            for (DocumentSnapshot doc : querySnapshot) {
//                Log.d("Firestore", "Notification: " + doc.getData());
//            }
//        });

        notificationsRef
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("Firestore", "Query returned " + querySnapshot.size() + " notifications.");
                    List<Map<String, Object>> filteredNotificationList = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> notificationData = doc.getData();
                        List<Map<String, Object>> recipients = (List<Map<String, Object>>) notificationData.get("recipients");
                        if (recipients != null) {
                            for (Map<String, Object> recipient : recipients) {
                                String recipientDeviceId = (String) recipient.get("deviceId");
                                if (deviceId.equals(recipientDeviceId)) {
                                    notificationData.put("notificationId", doc.getId());
                                    filteredNotificationList.add(notificationData);
                                    break;
                                }
                            }
                        }
                    }
                    Log.d("Firestore", "Filtered notifications count: " + filteredNotificationList.size());
                    callback.onSuccess(filteredNotificationList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "Query failed", e);
                    callback.onError(e);
                });
    }
}




