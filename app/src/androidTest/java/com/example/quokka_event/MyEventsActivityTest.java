package com.example.quokka_event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import com.example.quokka_event.controllers.MyEventsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) @LargeTest

public class MyEventsPageActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    // Test the activity switched (ESPRESSO TEST)
    @Test
    public void testActivity(){
        Intents.init();
        // Click on the My Events button
        onView(withId(R.id.my_events_button)).perform(click());

        // Check the Activity has changed
        intended(hasComponent(MyEventsActivity.class.getName()));
        Intents.release();

    }

    // Test the activity switched back (ESPRESSO TEST)
    @Test
    public void testBackButton(){
        // Click on the My Events button
        onView(withId(R.id.my_events_button)).perform(click());

        // Click on the Back button
        onView(withId(R.id.back_button_bottom)).perform(click());

        // Check the Activity has changed
        onView(withId(R.id.my_events_button)).check(matches(isDisplayed()));

    }
}

