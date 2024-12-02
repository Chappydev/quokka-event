package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.event.LotteryChecker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

/**
 * Displays event details for admins while navigating tabs
 */
public class AdminEventTabsActivity extends AppCompatActivity {
    Map<String, Object> event_details;
    private int  currIndex;
    Button backButton;
    Button deleteButton;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference posterPicRef;

    /**
     * Initializes activity, sets up tab layout and UI
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_tabs);
        backButton = findViewById(R.id.cancel_button);
        deleteButton = findViewById(R.id.delete_button);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            event_details = (Map<String, Object>) extras.get("event");
            currIndex = (int) extras.get("index");
        }
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        DatabaseManager db = DatabaseManager.getInstance(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if (event_details.get("posterImagePath") != null && event_details.get("eventId") != null) {
            posterPicRef = storageRef.child("Events/"+event_details.get("eventId")+".jpg");
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Return to event list when clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Allow the admin to delete the event from the database when button is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                String eventId = (String)event_details.get("eventId");
                db.deleteEvent(eventId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        if (posterPicRef != null) {
                            posterPicRef.delete()
                                    .addOnFailureListener(new OnFailureListener() {
                                        /**
                                         * Display helpful error
                                         * @param e
                                         */
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (!(e instanceof StorageException) || ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                                Log.e("AdminEventsTabActivity", "delete button onFailure: ", e);
                                                Log.d("AdminEventsTabActivity", "onFailure: " + (String) event_details.get("posterImagePath"));
                                                Log.d("AdminEventsTabActivity", "onFailure: " +  posterPicRef);
                                                Toast.makeText(AdminEventTabsActivity.this,
                                                        "Something went wrong deleting the associated image",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("AdminEventsTabActivity", "Image is linked but doesn't exist: delete button onFailure: ", e);
                                            }
                                            returnToEventList();
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            returnToEventList();
                                        }
                                    });
                        }
                        returnToEventList();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("AdminEventsTabActivity", "onError: ", exception);
                    }
                });
            }
        });

        // Add debug lottery button
        Button debugLotteryButton = findViewById(R.id.debug_lottery_button);
        debugLotteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLottery();
            }
        });

    }

    /**
     * Set event details
     * @return Map with all event details
     */
    public Map<String, Object> getEventDetails(){
        return event_details;
    }

    /**
     * Set event details
     * @return Map with all event details
     */
    public int getCurrentIndex(){
        return currIndex;
    }

    /**
     * Return to events list for admins once event is deleted.
     */
    void returnToEventList(){
        Intent intent = new Intent(this, AdminBrowseEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void testLottery() {
        // Get event details from your existing event_details map
        String eventId = (String) event_details.get("eventId");
        String eventName = (String) event_details.get("eventName");
        long maxParticipants = (long) event_details.get("maxParticipants");

        // Create intent similar to what AlarmManager would create
        Intent testIntent = new Intent();
        testIntent.putExtra("eventId", eventId);
        testIntent.putExtra("eventName", eventName);
        testIntent.putExtra("maxParticipants", (int) maxParticipants);

        // Run the lottery
        LotteryChecker checker = new LotteryChecker();
        checker.onReceive(getApplicationContext(), testIntent);

        // Show a toast so we know it started
        Toast.makeText(this, "Running lottery test...", Toast.LENGTH_SHORT).show();
    }
}
