package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.OverviewFragment;
import com.example.quokka_event.models.event.QRFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {
    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminEventOverviewFragment();
            case 1:
                return new DetailsFragment();
            case 2:
                return new QRFragment();
            default:
                return new OverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
