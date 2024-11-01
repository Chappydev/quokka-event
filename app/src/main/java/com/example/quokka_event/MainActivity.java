package com.example.quokka_event;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.WaitlistActivity;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;
import com.example.quokka_event.models.ProfileSystem;
import com.example.quokka_event.models.event.MyEventsPageFragment;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button myEventButton;
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

        DatabaseManager db = DatabaseManager.getInstance(this);

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



    }

    // Switch activity to WaitlistActivity TEMPORARY FOR TESTING WAITLIST ACTIVITY.
    private void switchActivities(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.my_events_page, new MyEventsPageFragment());
        fragmentTransaction.addToBackStack(null); // Optional
        fragmentTransaction.commit();
    }
}