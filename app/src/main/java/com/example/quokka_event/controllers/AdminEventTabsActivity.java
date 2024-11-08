package com.example.quokka_event.controllers;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Map;

/**
 * Event tabs for admin browsing.
 */
public class AdminEventTabsActivity extends AppCompatActivity {
    Map<String, Object> event_details;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_tabs);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            event_details = (Map<String, Object>) extras.get("event");
        }
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Overview");
                    break;
                case 1:
                    tab.setText("Details");
                    break;
                case 2:
                    tab.setText("QR Code");
                    break;
            }
        }).attach();
    }
    public Map<String, Object> getEventDetails(){
        return event_details;
    }
}
