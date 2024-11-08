package com.example.quokka_event;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.controllers.AdminLandingPageActivity;
import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.controllers.MyEventsActivity;
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
    private Button myEventButton;
    private String lastCreatedEventId;
    private String lastCreatedFacilityId;
    private static final int REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_landing_page);

        myEventButton = findViewById(R.id.my_events_button);

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

        // Switch the activity to MyEventsActivity when the myEventsButton is clicked
        myEventButton = findViewById(R.id.my_events_button);
        myEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent showActivity = new Intent(MainActivity.this, MyEventsActivity.class);
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

        // Switch the activity to the QrScannerPageActivity when the scan qr code button is clicked
        final Button scanQrCodeButton = findViewById(R.id.scan_qr_button);
        scanQrCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
                } else {
                    Intent showActivity = new Intent(MainActivity.this, QrScannerPageActivity.class);
                    MainActivity.this.startActivity(showActivity);
                }
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
                switchAdminActivity(user.isAdmin());
            }

            @Override
            public void onError(Exception exception) {
                Log.e("DB", "onError: ", exception);
            }
        }, uid);



    }

    // Switch activity to WaitlistActivity TEMPORARY FOR TESTING WAITLIST ACTIVITY.
    private void switchActivities(){
        Intent intent = new Intent(this, MyEventsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("QRScan", "onRequestPermissionsResult: " + grantResults[0]);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent showActivity = new Intent(MainActivity.this, QrScannerPageActivity.class);
                MainActivity.this.startActivity(showActivity);
            } else {
                Toast.makeText(this, "Cannot use the scanner without camera permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * start admin landing page
     */
    void switchAdminActivity (Boolean isAdmin){
        if (isAdmin){
            Intent activity = new Intent(MainActivity.this, AdminLandingPageActivity.class);
            startActivity(activity);
        }

    }
}
