package com.example.quokka_event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

import android.widget.DatePicker;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import com.example.quokka_event.controllers.EventTabsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) @LargeTest

public class NotifyParticipantsFragTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    // Test the activity switched (ESPRESSO TEST)
    @Test
    public void testActivity(){
        Intents.init();
        // Click on the Organizer events button
        onView(withId(R.id.organizer_events_button)).perform(click());

        // Check the Activity has changed
        intended(hasComponent(OrganizerEventsPageActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testAddEvent(){
        Intents.init();
        // Click on the add event button
        onView(withId(R.id.add_button_bottom)).perform(click());
        // Check the Activity has changed
        intended(hasComponent(EventTabsActivity.class.getName()));
        Intents.release();
        // Click on the save event button
        onView(withId(R.id.savebutton)).perform(click());
        // Expect that it wont let you save
        onView(withId(R.id.event_title_label)).check(matches(withText("Event Name")));
        // Click on the edit title button and type an event title
        onView(withId(R.id.edit_title_button)).perform(click()).perform(typeText("Test Event"));
        // Click on the positive button to save
        onView(withText("CONFIRM")).perform(click());
        // Check that the EditText has changed to "Test Event"
        onView(withId(R.id.event_title_label)).check(matches(withText("Test Event")));
        // Try to save the event, expect that you wont be able to
        onView(withText("CONFIRM")).perform(click());
        onView(withId(R.id.event_title_label)).check(matches(withText("Test Event")));
        // Click on the edit dtl button
        onView(withId(R.id.edit_dtl_button)).perform(click());
        // Choose dtl fields
        onView(withText("Date:")).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName()))).perform(setDate(2024, 12, 30));
        onView(withText("OK")).perform(click());
        // Click on the text with "Deadline:" and choose the number 31
        onView(withText("Deadline:")).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName()))).perform(setDate(2024, 12, 31));
        onView(withText("OK")).perform(click()); // Click on the text with "Location:" and type "Here" onView(withText("Location:")).perform(click()); onView(withId(R.id.location_input)).perform(typeText("Here")); onView(withText("Save")).perform(click());

    }

}
