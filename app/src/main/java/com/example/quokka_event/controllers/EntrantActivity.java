package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;

public class EntrantActivity extends AppCompatActivity {
    private TextView eventTitle;
    private TextView dateText;
    private TextView timeText;
    private TextView locationText;
    private TextView organizerText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.event_waitlist_details);
    }
}
