package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.controllers.AdminLandingPageActivity;
import com.example.quokka_event.controllers.CreateProfileActivity;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.MyEventsPageActivity;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.ProfileSystem;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageButton profileButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_landing_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseManager db = DatabaseManager.getInstance(this);
        profileButton = findViewById(R.id.profile);
        User user = User.getInstance(this);
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.getDeviceUser(new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                user.initialize(
                        (String) map.get("deviceID"),
                        (ProfileSystem) map.get("profile"),
                        (Boolean) map.get("organizer"),
                        (Boolean) map.get("admin")
                );
                Log.d("DB", "onCreate: " + user.getDeviceID());
            }
            @Override
            public void onError(Exception exception) {
                Log.e("DB", "onError: ", exception);
            }
        }, deviceId);


        profileButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the profile image is clicked open CreateProfileActivity.java
             * @param view
             * */
            @Override
                public void onClick(View view) {
                    switchToProfileActivity();

                }
            });


        // Switch the activity to MyEventsActivity when the myEventsButton is clicked
        final Button myEventsButton = findViewById(R.id.my_events_button);
        myEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(MainActivity.this, MyEventsPageActivity.class);
                startActivity(showActivity);
            }
        });

        // Switch the activity to the NotificationPageActivity when the bell icon is clicked
        final ImageButton bellButton = findViewById(R.id.bell);
        bellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(MainActivity.this, NotificationPageActivity.class);
                startActivity(showActivity);
            }
        });

        // Switch the activity to the OrganizerEventsPageActivity when the organizer events button is clicked
        final Button organizerEventsButton = findViewById(R.id.organizer_events_button);
        organizerEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(MainActivity.this, OrganizerEventsPageActivity.class);
                startActivity(showActivity);
            }
        });
        Intent showAdminActivity = new Intent(MainActivity.this, AdminLandingPageActivity.class);
        startActivity(showAdminActivity);
    }

    /**
     * Function to switch to CreateProfileActivity.java
     *
     * */
    private void switchToProfileActivity(){
        Intent switchActivity = new Intent(this, CreateProfileActivity.class);
        startActivity(switchActivity);
    }

}