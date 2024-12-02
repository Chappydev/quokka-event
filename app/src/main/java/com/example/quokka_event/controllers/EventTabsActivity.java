package com.example.quokka_event.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.NotificationPageActivity;
import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.Event;
import com.example.quokka_event.models.event.EventLotteryManager;
import com.example.quokka_event.models.event.OverviewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages the event creation and editing interface with Firebase integration, event validation, and image upload
 */
public class EventTabsActivity extends AppCompatActivity implements OverviewFragment.overviewEditListener, DetailsFragment.detailsListener, DetailsFragment.geolocationListener {
    Button saveButton;
    Button cancelButton;
    Event event;
    FirebaseAuth auth;
    private DatabaseManager db;

    //image
    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    /**
     * Setup EventTabsActivity. Have two tabs for setting/display event details.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_tabs);
        event = new Event();
        Date currentDate = new Date();
        event.setEventName("Event");
        event.setEventLocation("Location");
        event.setEventDate(currentDate);
        event.setRegistrationDeadline(currentDate);
        event.setDescription("");
        event.setMaxWaitlist(Integer.MAX_VALUE);
        event.setMaxParticipants(Integer.MIN_VALUE);
        event.setGeolocationEnabled(false);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        auth = FirebaseAuth.getInstance();
        DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());

        // Set up the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        saveButton = findViewById(R.id.savebutton);
        cancelButton = findViewById(R.id.cancelbutton);

        // Connect TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Details");
                    break;

            }
        }).attach();

        // Switch the activity to the NotificationPageActivity when the bell icon is clicked
        final ImageButton bellButton = findViewById(R.id.bell);
        bellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(EventTabsActivity.this, NotificationPageActivity.class);
                startActivity(showActivity);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name  = event.getEventName();
                String location = event.getEventLocation();
                Date eventDate = event.getEventDate();
                Date registrationDate = event.getRegistrationDeadline();
                long maxEntrants = event.getMaxWaitlist();
                int maxParticipants = event.getMaxParticipants();

                Log.d("max p", "onClick: " + maxParticipants);

                if (maxParticipants == 0){
                    Toast.makeText(EventTabsActivity.this, "Please enter a participant limit!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Valid Entry Logic
                if (name.trim().isEmpty() || location.trim().isEmpty() || eventDate == null || registrationDate == null || maxParticipants == Integer.MIN_VALUE) {
                    Toast.makeText(EventTabsActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.length() > 100) {
                    Toast.makeText(EventTabsActivity.this, "Please limit book name to 50 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Check if either date is in the past
                    if (eventDate.before(currentDate) || registrationDate.before(currentDate)) {
                        Toast.makeText(EventTabsActivity.this, "Please enter a future date and time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Ensure event date is after the registration deadline
                    if (eventDate.before(registrationDate)) {
                        Toast.makeText(EventTabsActivity.this, "Event date must be AFTER the registration deadline", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate the year range for both dates
                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                    int eventYear = Integer.parseInt(yearFormat.format(eventDate));
                    int registrationYear = Integer.parseInt(yearFormat.format(registrationDate));

                    if (eventYear < 2024 || eventYear > 3000 || registrationYear < 2024 || registrationYear > 3000) {
                        Toast.makeText(EventTabsActivity.this, "Please enter a valid year (2024-3000)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(EventTabsActivity.this, "An unexpected error occurred. Please check your input.", Toast.LENGTH_SHORT).show();
                    Log.e("DateValidation", "Error validating dates");
                    return;
                }

                String deviceId = auth.getCurrentUser().getUid();

                // Save the event to the database first
                db.addEvent(event, deviceId, new DbCallback()
                {
                    @Override
                    public void onSuccess(Object result)
                    {
                        String eventID = result.toString(); // firebase id for the evnt
                        event.setEventID(eventID);
                        Log.d("ImageDebug", eventID);
                        Toast.makeText(EventTabsActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();

                        EventLotteryManager elm = new EventLotteryManager();
                        long deadline = event.getRegistrationDeadline().getTime();
                        elm.deadlineLottery(getApplicationContext(), event,deadline);
                        finish();

                        // check if there is an image to upload
                        if (imageUri != null) {
                            StorageReference imageRef = storageReference.child("Events/" + eventID + ".jpg");

                            // Upload the image to Firebase Storage
                            imageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        Log.d("ImageDebug", "File uploaded successfully.");
                                        // Get the download URL after upload
                                        imageRef.getDownloadUrl()
                                                .addOnSuccessListener(downloadUrl -> {
                                                    String imagePath = downloadUrl.toString();
                                                    Log.d("ImageDebug", "Download URL: " + imagePath);

                                                    // Link image to the saved event
                                                    db.addImageToEvent(eventID, imagePath, new DbCallback() {
                                                        @Override
                                                        public void onSuccess(Object result) {
                                                            Toast.makeText(EventTabsActivity.this, "Image linked to event successfully!", Toast.LENGTH_SHORT).show();
                                                            finish(); // Close the activity
                                                        }

                                                        @Override
                                                        public void onError(Exception exception) {
                                                            Toast.makeText(EventTabsActivity.this, "Failed to link image to event", Toast.LENGTH_SHORT).show();
                                                            Log.e("DB", "Error linking image", exception);
                                                        }
                                                    });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(EventTabsActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                                    Log.e("Firebase", "Error getting download URL", e);
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EventTabsActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                                        Log.e("Firebase", "Image upload error", e);
                                    });
//
                        } else {
                            String deviceId = auth.getCurrentUser().getUid();
                            db.addEvent(event, deviceId, new DbCallback() {
                                @Override
                                public void onSuccess(Object result) {
                                    Log.d("DB", "added Event: " + event.getEventName() + " to database");
                                    Toast.makeText(EventTabsActivity.this,
                                            "Event created successfully without poster",
                                            Toast.LENGTH_SHORT).show();

                                    finish();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Log.e("DB", "Error creating event: ", exception);
                                    Toast.makeText(EventTabsActivity.this,
                                            "Error creating event",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(EventTabsActivity.this, "Error creating event", Toast.LENGTH_SHORT).show();
                        Log.e("DB", "Error creating event", exception);
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Sets the image uri for the event
     * @param uri
     */
    public void setImageUri(Uri uri) {
        this.imageUri = uri;
    }

    /**
     * Uploads image to Firebase and saves the event
     */
    private void uploadImageAndSaveEvent() {
        String eventID = event.getEventID();
        StorageReference imageRef = storageReference.child("Events/" + eventID + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUrl -> {
                                saveEvent(downloadUrl.toString());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                Log.e("Firebase", "Error getting download URL", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Image upload error", e);
                });
    }

    /**
     * Saves the event to Firebase with  image URL
     * @param imageUrl
     */
    private void saveEvent(String imageUrl) {
        event.setImageUrl(imageUrl); // Assuming Event class has setImageUrl()

        String deviceId = auth.getCurrentUser().getUid();
        db.addEvent(event, deviceId, new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Toast.makeText(EventTabsActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(EventTabsActivity.this, "Error creating event", Toast.LENGTH_SHORT).show();
                Log.e("DB", "Error creating event", exception);
            }
        });
    }


    /**
     * Called from listener from overviewfragment.java to set event name
     *
     * @param eventTitle
     */
    @Override
    public void setEventName(String eventTitle) {
        event.setEventName(eventTitle);
    }

    /**
     * Called from listener from DetailsFragment.java to set event date
     *
     * @param eventDate
     */
    @Override
    public void setEventDate(Date eventDate) {
        event.setEventDate(eventDate);
    }

    /**
     * Set event location called from a listener from DetailsFragment.java
     * @param location
     */
    @Override
    public void setLocation(String location) {
        event.setEventLocation(location);
    }

    /**
     * Set event deadline called from a listener in DetailsFragment.java
     * @param deadline
     */
    @Override
    public void setDeadline(Date deadline) {
        event.setRegistrationDeadline(deadline);
    }

    @Override
    public void setDescription(String description) { event.setDescription(description); }

    /**
     * Called from listener in DetailsFragment.java
     * @param waitlistCap
     * @param partCap
     */
    @Override
    public void setCapacity(int waitlistCap, int partCap) {
        Log.d("setCapacity", "setCapacity: "+partCap);
        Log.d("setCapacity", "setCapacity: "+waitlistCap);
        event.setMaxWaitlist(waitlistCap);
        event.setMaxParticipants(partCap);
        Log.d("setCapacity", "setCapacity: "+event.getMaxParticipants());
        Log.d("setCapacity", "setCapacity: "+event.getMaxWaitlist());
    }

    @Override
    public void setGeolocation(Boolean geolocationEnabled) {
        event.setGeolocationEnabled(geolocationEnabled);
    }
}

