package com.example.quokka_event;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
//import androidx.test.espresso.contrib.RecyclerViewActions;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) @LargeTest

public class UserEventsUIFlowTests {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * ESPRESSO TEST
     * Check that the activity switches to the users event list when the My Events Button is pressed
     * @author mylayambao
     * @since project part 3
     */
    @Test
    public void testActivity(){

        // Click on the My Events button
        onView(withId(R.id.my_events_button)).perform(click());

        // Check the Activity has changed
        onView(withId(R.id.event_list_recycler_view)).check(matches(isDisplayed()));

    }

    /**
     * ESPRESSO TEST
     * Check that the activity switches back to the landing page from the user's event list page.
     * @author mylayambao
     * @since project part 3
     */
    @Test
    public void testBackButton(){
        // Click on the My Events button
        onView(withId(R.id.my_events_button)).perform(click());

        // Click on the Back button
        onView(withId(R.id.back_button_bottom)).perform(click());

        // Check the Activity has changed
        onView(withId(R.id.my_events_button)).check(matches(isDisplayed()));

    }

    /**
     * ESPRESSO TEST
     * Check that the activity to the event details of an event when clicked.
     * @author mylayambao
     * @since project part 3
     */
    @Test
    public void testEventDetails(){
        // Click on the My Events button
        onView(withId(R.id.my_events_button)).perform(click());

        // Click on an event
        onView(Matchers.allOf(withId(R.id.event_list_recycler_view), hasDescendant(withText("Event 3"))))
                .perform(click());

        // Check the Activity has changed
        onView(withId(R.id.event_name_text)).check(matches(isDisplayed()));

    }
}
