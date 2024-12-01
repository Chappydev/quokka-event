package com.example.quokka_event.controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Adapter for managing even-related fragments for admins
 */
public class AdminEventViewPager extends ViewPagerAdapter{

    /**
     * Creates a new instance of AdminEventViewPager
     * @param fragmentActivity
     */
    public AdminEventViewPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

}
