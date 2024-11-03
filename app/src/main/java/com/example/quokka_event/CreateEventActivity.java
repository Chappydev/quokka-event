package com.example.quokka_event;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class CreateEventActivity extends AppCompatActivity implements EditEventTitleFragment.EditTitleDialogListener {
    TabLayout organizerEventsTab;
    ViewPager2 viewPager2;
    CreateEventTabsAdapter createEventAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);
        organizerEventsTab = findViewById(R.id.organizer_tabs);
        viewPager2 = findViewById(R.id.event_viewpager);
        createEventAdapter = new CreateEventTabsAdapter(this);
        viewPager2.setAdapter(createEventAdapter);
        organizerEventsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                organizerEventsTab.getTabAt(position).select();
            }
        });
    }

    @Override
    public void editEventTitle(String eventTitle) {

    }

    public class CreateEventTabsAdapter extends FragmentStateAdapter{
        public CreateEventTabsAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position){
            switch (position){
                case 0: return new OrganizerEditEventFragment();
                default: return new OrganizerEditEventFragment();
            }
        }

        @Override
        public int getItemCount(){
            return 3;
        }
    }

}
