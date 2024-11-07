package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.controllers.MyEventsPageActivity;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.ProfileSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseManager db;
    private Button myEventsButton;
    private String lastCreatedEventId;
    private String lastCreatedFacilityId;
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

        db = DatabaseManager.getInstance(this);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            auth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String TAG = "AUTH";
                            if (task.isSuccessful()) {
                                initUser(task.getResult().getUser().getUid());
                            } else {
                                Log.e(TAG, "onComplete: ", task.getException());
                            }
                        }
                    });
        } else {
            initUser(currentUser.getUid());
        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//
////        updateUI(currentUser);
//    }

    private void initUser(String uid) {
        User user = User.getInstance(this);
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
        }, uid);


        // Switch the activity to MyEventsActivity when the myEventsButton is clicked
        myEventsButton = findViewById(R.id.my_events_button);
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
                MainActivity.this.startActivity(showActivity);
            }
        });

        // Switch the activity to the UserProfilePageActivity when the person icon is clicked
        final ImageButton profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(MainActivity.this, UserProfilePageActivity.class);
                MainActivity.this.startActivity(showActivity);
            }
        });

        // Switch the activity to the OrganizerEventsPageActivity when the organizer events button is clicked
        final Button organizerEventsButton = findViewById(R.id.organizer_events_button);
        organizerEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(MainActivity.this, OrganizerEventsPageActivity.class);
                MainActivity.this.startActivity(showActivity);
            }
        });

    }

}