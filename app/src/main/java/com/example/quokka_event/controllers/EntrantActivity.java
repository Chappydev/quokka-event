package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quokka_event.R;
import com.example.quokka_event.models.entrant.EventManager;

public class EntrantActivity extends AppCompatActivity {
    private EventManager eventManager;

    private TextView waitlistJoinConfirmation;
    private Button exitButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.eventlist_activity);
    }
}
