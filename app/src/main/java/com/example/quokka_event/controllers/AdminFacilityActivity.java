package com.example.quokka_event.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quokka_event.R;
import com.example.quokka_event.views.AdminEventsAdapter;
import com.example.quokka_event.views.AdminFacilityAdapter;
import com.example.quokka_event.views.ViewButtonListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class AdminFacilityActivity extends AppCompatActivity implements ViewButtonListener {
    private ArrayList<Map<String, Object>> facilityList;
    private AdminFacilityAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_activity);
        RecyclerView facilityRecyclerView = findViewById(R.id.admin_facility_list);
        DatabaseManager db = DatabaseManager.getInstance(this);
        facilityList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        facilityRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdminFacilityAdapter(facilityList, this);
        db.getAllFacilities(new DatabaseManager.RetrieveData() {
            @Override
            public void onDataLoaded(ArrayList<Map<String, Object>> list) {
                facilityList.addAll(list);
                adapter.setLocalDataSet(list);
                facilityRecyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void viewButtonClick(int pos) {
        Intent activity = new Intent(AdminFacilityActivity.this, AdminFacilityDetails.class);
        activity.putExtra("facility", (Serializable) facilityList.get(pos));
        startActivity(activity);
    }
}
