package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.views.AdminEventsAdapter;
import com.example.quokka_event.views.ViewButtonListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class AdminBrowseEventsActivity extends AppCompatActivity implements ViewButtonListener {
    private ArrayList<Map<String, Object>> eventList;
    private AdminEventsAdapter adapter;
    Button backButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_events);
        RecyclerView eventRecyclerView = findViewById(R.id.admin_events_list);
        backButton = findViewById(R.id.admin_event_back);
        DatabaseManager db = DatabaseManager.getInstance(this);
        eventList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        eventRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdminEventsAdapter(eventList, this);
        db.getAllEvents(new DatabaseManager.RetrieveData() {
            @Override
            public void onDataLoaded(ArrayList<Map<String, Object>> list) {
                eventList.addAll(list);
                adapter.setLocalDataSet(list);
                eventRecyclerView.setAdapter(adapter);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Back button to return to LandingPage
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void viewButtonClick(int pos) {
        Intent activity = new Intent(AdminBrowseEventsActivity.this, AdminEventTabsActivity.class);
        activity.putExtra("event", (Serializable) eventList.get(pos));
        startActivity(activity);

    }


}
