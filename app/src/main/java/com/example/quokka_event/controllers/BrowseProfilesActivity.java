package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.example.quokka_event.models.ProfileSystem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class BrowseProfilesActivity extends AppCompatActivity {
    DatabaseManager db;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_profile);

        TabLayout tabLayout = findViewById(R.id.admin_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.admin_view_pager);

        AdminBrowseProfileAdapter adapter = new AdminBrowseProfileAdapter(this);
        viewPager.setAdapter(adapter);
        db = DatabaseManager.getInstance(this);
        ArrayList<ProfileSystem> users = db.getAllProfiles();
        Log.d("findDB", Integer.toString(users.size()));


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
           switch (position){
               case 0:
                   tab.setText("All Profiles");
                   break;
               case 1:
                   tab.setText("Violations");
           }
        }).attach();


    }
}
