package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quokka_event.models.event.EventAttendingFragment;
import com.example.quokka_event.models.event.EventCancelledFragment;
import com.example.quokka_event.models.event.EventInvitedFragment;
import com.example.quokka_event.models.event.EventWaitlistFragment;

/**
 * Adapter for managing event entrant tabs. Handles switching between fragments
 */
public class ViewPagerAdapterEventEntrant extends FragmentStateAdapter {
    private final String eventId;

    /**
     * Constructs the ViewPagerAdapterEventEntrant with FragmentActivity and event ID
     * @param fragmentActivity
     * @param eventId
     */
    public ViewPagerAdapterEventEntrant(@NonNull FragmentActivity fragmentActivity, @NonNull String eventId) {
        super(fragmentActivity);
        this.eventId = eventId;
    }

    /**
     * Creates a fragment based on the position in the ViewPager2
     * @param position
     * @return The corresponding fragment for the given position
     */
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

    /**
     * Returns the number of tabs
     * @return The total number of tabs (4)
     */
    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}