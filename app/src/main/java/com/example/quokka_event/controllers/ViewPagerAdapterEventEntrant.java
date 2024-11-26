package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.EventAttendingFragment;
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
                return EventAttendingFragment.newInstance("wRqJ5v3rx9QTcnfSMnWw");
            case 1:
                return EventWaitlistFragment.newInstance("wRqJ5v3rx9QTcnfSMnWw");
            case 2:
                return EventWaitlistFragment.newInstance("wRqJ5v3rx9QTcnfSMnWw");
            case 3:
                return EventWaitlistFragment.newInstance("wRqJ5v3rx9QTcnfSMnWw");
            default:
                return EventWaitlistFragment.newInstance("wRqJ5v3rx9QTcnfSMnWw");
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}


