package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;

public class AdminBrowseEventsActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_events);
        RecyclerView eventRecyclerView = findViewById(R.id.admin_events_list);
        DatabaseManager db = DatabaseManager.getInstance(this);

    }
}
