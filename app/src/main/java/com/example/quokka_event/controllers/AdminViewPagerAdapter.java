package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.OverviewFragment;
import com.example.quokka_event.models.event.QRFragment;

/**
 * Adapter for managing fragments in the admin event view pager
 * It handles the management of these fragments
 */
public class AdminViewPagerAdapter extends FragmentStateAdapter {
    /**
     * Adapter for managing fragments in the admin event view pager
     * @param fragmentActivity Activity with the fragments
     */
    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates fragment for given position
     * @param position Position of the fragment
     * @return New instance of AdminEventOverviewFragment
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new AdminEventOverviewFragment();
    }

    /**
     * Returns number of fragments in the view pager
     * @return The number of fragments
     */
    @Override
    public int getItemCount() {
        return 1;
    }
}
