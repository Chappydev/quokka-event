package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.OverviewFragment;

/**
 * Adapter for managing fragments. Handles switching between Overview and Details fragments.
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructs ViewPagerAdapter with FragmentActivity.
     * @param fragmentActivity The activity hosting the ViewPager2
     */
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates a fragment based on the position in the ViewPager2
     * @param position The position of the tab in the ViewPager2
     * @return The corresponding fragment for the given position
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OverviewFragment();
            case 1:
                return new DetailsFragment();
            default:
                return new OverviewFragment();
        }
    }

    /**
     * Returns the number of tabs in the ViewPager2
     * @return The total number of tabs
     */
    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}