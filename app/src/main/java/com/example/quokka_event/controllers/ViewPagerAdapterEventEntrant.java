package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.EventWaitlistFragment;
import com.example.quokka_event.models.event.OverviewFragment;
import com.example.quokka_event.models.event.QRFragment;

public class ViewPagerAdapterEventEntrant extends FragmentStateAdapter {

    public ViewPagerAdapterEventEntrant(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EventWaitlistFragment();
            case 1:
                return new EventWaitlistFragment();
            case 2:
                return EventWaitlistFragment.newInstance("wRqJ5v3rx9QTcnfSMnWw");
            case 3:
                return new EventWaitlistFragment();
            default:
                return new EventWaitlistFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}


