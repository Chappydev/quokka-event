package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.OverviewFragment;
import com.example.quokka_event.models.event.QRFragment;

/**
 * Adapter for profiles in a list in admin fragment. Displays based on tab selected
 */
public class AdminBrowseProfileAdapter extends FragmentStateAdapter{
    /**
     * AdminBrowseProfileAdapter Constructor
     * @param fragmentActivity FragmentActivity
     */
    public AdminBrowseProfileAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Show a fragment depending on tab
     * @param position The position of the tab.
     * @return The fragment to be displayed
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileListFragment();
            default:
                return new ProfileListFragment();
        }

    }

    /**
     * Getter for the total number of tabs
     * @return The number of tabs (currently at 1)
     */
    @Override
    public int getItemCount() {
        return 1;
    }
}