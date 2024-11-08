package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.controllers.dbutil.DbCallback;

import java.util.Map;

/**
 * Activity where the user's details are displayed for admins.
 * Shows name, email, phone
 */
public class BrowseProfileDetailsActivity extends AppCompatActivity {
    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;
    Button backButton;
    Button deleteButton;
    /**
     * Create the profile details activity for admin. Get Bundle that contains
     * profile details as a Map<String, Object> object from ProfileListFragment.java
     * Set the textview to match profile details.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);
        nameTextView = findViewById(R.id.name_details_text);
        emailTextView = findViewById(R.id.email_details_text);
        phoneTextView = findViewById(R.id.phone_details_text);
        backButton = findViewById(R.id.admin_back_button);
        deleteButton = findViewById(R.id.admin_delete_profile_button);

        String deviceId = "";
        Bundle extras = getIntent().getExtras();
        DatabaseManager db = DatabaseManager.getInstance(this);
        if (extras != null) {
            Map<String, Object> profile = (Map<String, Object>) extras.getSerializable("profile");

            nameTextView.setText((String) profile.get("name"));
            emailTextView.setText((String) profile.get("email"));
            phoneTextView.setText((String) profile.get("phone"));
            deviceId = (String) profile.get("deviceID");
            //The key argument here must match that used in the other activity
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Onclick event for back button. Closes the activity to go back.
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String finalDeviceId = deviceId;
        Log.d("DBupdate", deviceId);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When delete button is clicked, delete the profile using {@link DatabaseManager} deleteProfile()
             * @param view
             */
            @Override
            public void onClick(View view) {
                Log.d("DBupdate", finalDeviceId);
                db.deleteProfile(finalDeviceId, new DbCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Log.d("db","user deleted!");
                        startBrowseProfiles();
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            }
        });
    }

    /**
     * Start the browse profile again and clear this activity.
     */
    void startBrowseProfiles(){
        Intent intent = new Intent(this, BrowseProfilesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
