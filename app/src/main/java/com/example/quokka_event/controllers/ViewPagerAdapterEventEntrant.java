package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.DetailsFragment;
import com.example.quokka_event.models.event.EventAttendingFragment;
import com.example.quokka_event.models.event.EventCancelledFragment;
import com.example.quokka_event.models.event.EventInvitedFragment;
import com.example.quokka_event.models.event.EventWaitlistFragment;
import com.example.quokka_event.models.event.OverviewFragment;
import com.example.quokka_event.models.event.QRFragment;

public class ViewPagerAdapterEventEntrant extends FragmentStateAdapter {

    private final String eventId;
    public ViewPagerAdapterEventEntrant(@NonNull FragmentActivity fragmentActivity, @NonNull String eventId) {
        super(fragmentActivity);
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return EventAttendingFragment.newInstance(eventId);
            case 1:
                return EventInvitedFragment.newInstance(eventId);
            case 2:
                return EventWaitlistFragment.newInstance(eventId);
            case 3:
                return EventCancelledFragment.newInstance(eventId);
            default:
                return EventWaitlistFragment.newInstance(eventId);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}


