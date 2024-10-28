package com.example.quokka_event;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quokka_event.controllers.DatabaseManager;
import com.example.quokka_event.controllers.dbutil.DbCallback;
import com.example.quokka_event.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseManager db = DatabaseManager.getInstance(this);

//        db.initDeviceUser();
        db.getUserMap(new DbCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.d("DB", "In callback: " + result.toString());
                // do other stuff like initializing User class based on this data
            }
            @Override
            public void onError(Exception exception) {
                Log.e("DB", "onError: ", exception);
            }
        });
        User user = User.getInstance(this);
        Log.d("DB", "onCreate: " + user.getDeviceID() + ", ");
    }
}