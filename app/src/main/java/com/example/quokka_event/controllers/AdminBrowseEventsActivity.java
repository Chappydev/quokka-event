package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;

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

/**
 * This class displays a list of events to the admin. Admins can also view details.
 */
public class AdminBrowseEventsActivity extends AppCompatActivity implements ViewButtonListener {
    private ArrayList<Map<String, Object>> eventList;
    private AdminEventsAdapter adapter;

    /**
     * Sets up the RecyclerView and initialize the event list from firebase
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_events);
        RecyclerView eventRecyclerView = findViewById(R.id.admin_events_list);
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
    }

    /**
     * Passes the selected event data to the AdminEventTabsActivity
     * @param pos The position of the clicked event in the list
     */
    @Override
    public void viewButtonClick(int pos) {
        Intent activity = new Intent(AdminBrowseEventsActivity.this, AdminEventTabsActivity.class);
        activity.putExtra("event", (Serializable) eventList.get(pos));
        startActivity(activity);

    }
}
