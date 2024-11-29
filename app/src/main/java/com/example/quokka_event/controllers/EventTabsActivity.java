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
import com.example.quokka_event.models.event.OverviewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.context.AttributeContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class EventTabsActivity extends AppCompatActivity implements OverviewFragment.overviewEditListener, DetailsFragment.detailsListener {
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
        event.setMaxParticipants(Integer.MAX_VALUE);

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
                String eventID = event.getEventID();
                String deviceId = auth.getCurrentUser().getUid();

                // Save the event to the database first
                db.addEvent(event, deviceId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Toast.makeText(EventTabsActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();

                        // Now check if there is an image to upload
                        if (imageUri != null) {
                            StorageReference imageRef = storageReference.child("Events/" + event.getEventName() + ".jpg");

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
//            @Override
//            public void onClick(View view) {
//                if (imageUri != null) {
//                    String eventID = event.getEventID();
//                    StorageReference imageRef = storageReference.child("Events/" + System.currentTimeMillis() + ".jpg");
//
//                    // Upload the image to Firebase Storage
//                    imageRef.putFile(imageUri)
//                            .addOnSuccessListener(taskSnapshot -> {
//                                Log.d("ImageDebug", "File uploaded successfully.");
//                                // Get the download URL after upload
//                                imageRef.getDownloadUrl()
//                                        .addOnSuccessListener(downloadUrl -> {
//                                            String imagePath = downloadUrl.toString();
//                                            Log.d("ImageDebug", "Download URL: " + imagePath);
//                                            // Link image to event in fb
//                                            db.addImageToEvent(eventID, imagePath, new DbCallback() {
//                                                @Override
//                                                public void onSuccess(Object result) {
//                                                    Log.d("DB", "Image linked to event successfully!");
//                                                    String deviceId = auth.getCurrentUser().getUid();
//                                                    db.addEvent(event, deviceId, new DbCallback() {
//                                                        @Override
//                                                        public void onSuccess(Object result) {
//                                                            Toast.makeText(EventTabsActivity.this, "Event created successfully with image", Toast.LENGTH_SHORT).show();
//                                                            finish();
//                                                        }
//
//                                                        @Override
//                                                        public void onError(Exception exception) {
//                                                            Toast.makeText(EventTabsActivity.this, "Error creating event", Toast.LENGTH_SHORT).show();
//                                                            Log.e("DB", "Error creating event", exception);
//                                                        }
//                                                    });
//                                                }
//
//                                                @Override
//                                                public void onError(Exception exception) {
//                                                    Toast.makeText(EventTabsActivity.this, "Failed to link image to event", Toast.LENGTH_SHORT).show();
//                                                    Log.e("DB", "Error linking image", exception);
//                                                }
//                                            });
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(EventTabsActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
//                                            Log.e("Firebase", "Error getting download URL", e);
//                                        });
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(EventTabsActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
//                                Log.e("Firebase", "Image upload error", e);
//                            });
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


//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String deviceId = auth.getCurrentUser().getUid();
//                db.addEvent(event, deviceId, new DbCallback() {
//                    @Override
//                    public void onSuccess(Object result) {
//                        Log.d("DB", "added Event: " + event.getEventName() + " to database");
//                        Toast.makeText(EventTabsActivity.this,
//                                "Event created successfully",
//                                Toast.LENGTH_SHORT).show();
//
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(Exception exception) {
//                        Log.e("DB", "Error creating event: ", exception);
//                        Toast.makeText(EventTabsActivity.this,
//                                "Error creating event",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//                db.addImageToEvent(event.getEventID(), );
//            }
//        });



        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * sets the image uri
     * @param uri
     */
    public void setImageUri(Uri uri) {
        this.imageUri = uri;
    }

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
     * @param eventTitle
     */
    @Override
    public void setEventName(String eventTitle) {
        event.setEventName(eventTitle);
    }

    /**
     * Called from listener from DetailsFragment.java to set event date
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
        event.setMaxWaitlist(waitlistCap);
        event.setMaxParticipants(partCap);
    }
}

