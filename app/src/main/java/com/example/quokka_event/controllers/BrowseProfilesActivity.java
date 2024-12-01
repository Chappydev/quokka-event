package com.example.quokka_event.controllers;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quokka_event.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity for browsing profiles for admins. Uses a ViewPager with tabs to display different sections.
 */
public class BrowseProfilesActivity extends AppCompatActivity{
    DatabaseManager db;

    /**
     * Initializes activity, sets up ViewPager and TabLayout
     * @param savedInstanceState This bundle has all the data in the fragment in case the fragment restarts
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_profile);

        TabLayout tabLayout = findViewById(R.id.admin_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.admin_view_pager);

        AdminBrowseProfileAdapter adapter = new AdminBrowseProfileAdapter(this);
        viewPager.setAdapter(adapter);

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