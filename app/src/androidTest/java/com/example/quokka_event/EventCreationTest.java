package com.example.quokka_event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventCreationTest {
    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Before
    public void setUp() {
        try {
            // Disable animations
            device.executeShellCommand("settings put global window_animation_scale 0");
            device.executeShellCommand("settings put global transition_animation_scale 0");
            device.executeShellCommand("settings put global animator_duration_scale 0");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * ESPRESSO TEST
     * Check that the app can navigate to event creation page
     */
    @Test
    public void aTestEventCreationUI() throws UiObjectNotFoundException {
        device.findObject(new UiSelector().text("While using the app")).click();
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());
        onView(withId(R.id.upload_poster)).check(matches(isDisplayed()));
    }

    /**
     * ESPRESSO TEST
     * Check that missing fields aren't possible
     */
    @Test
    public void testMissingFields() {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());
        onView(withId(R.id.savebutton)).perform(click());
        device.findObject(new UiSelector().textContains("Please fill out all fields")).exists();
    }

    /**
     * ESPRESSO TEST
     * Check that event name isn't over a 100 characters
     */
    @Test
    public void testInvalidEventName() throws UiObjectNotFoundException {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());

        onView(withId(R.id.edit_title_button)).perform(click());
        onView(withId(R.id.event_title_edittext)).perform(typeText("This is a very long event name that will not be used ever in the history of ever. This is a very long event name that will not be used ever in the history of ever."));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.edit_dtl_button)).perform(click());
        onView(withId(R.id.editDateTextView)).perform(click());
        device.findObject(new UiSelector().text("23")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editDeadlineTextView)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("TestFacility"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.event_description)).perform(typeText("TestDescription"));

        onView(withId(R.id.savebutton)).perform(click());
        device.findObject(new UiSelector().textContains("Please limit event name to 100 characters")).exists();
    }

    /**
     * ESPRESSO TEST
     * Check that past dates aren't possible
     */
    @Test
    public void testPastDates() throws UiObjectNotFoundException {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());

        onView(withId(R.id.edit_title_button)).perform(click());
        onView(withId(R.id.event_title_edittext)).perform(typeText("TestEventName"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.edit_dtl_button)).perform(click());
        onView(withId(R.id.editDateTextView)).perform(click());
        device.findObject(new UiSelector().text("1")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("TestFacility"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.event_description)).perform(typeText("TestDescription"));

        onView(withId(R.id.savebutton)).perform(click());
        device.findObject(new UiSelector().textContains("Please enter a future date and time")).exists();
    }

    /**
     * ESPRESSO TEST
     * Check that event date isn't before registration deadline
     */
    @Test
    public void testInvalidTimelineDates() throws UiObjectNotFoundException {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());

        onView(withId(R.id.edit_title_button)).perform(click());
        onView(withId(R.id.event_title_edittext)).perform(typeText("TestEventName"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.edit_dtl_button)).perform(click());

        onView(withId(R.id.editDateTextView)).perform(click());
        device.findObject(new UiSelector().text("25")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editDeadlineTextView)).perform(click());
        device.findObject(new UiSelector().text("20")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("TestFacility"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.event_description)).perform(typeText("TestDescription"));

        onView(withId(R.id.savebutton)).perform(click());
        device.findObject(new UiSelector().textContains("Event date must be AFTER the registration deadline")).exists();
    }

    /**
     * ESPRESSO TEST
     * Check that years are between current year and 3000
     */
    @Test
    public void testInvalidYears() {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());

        onView(withId(R.id.edit_title_button)).perform(click());
        onView(withId(R.id.event_title_edittext)).perform(typeText("TestEventName"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.edit_dtl_button)).perform(click());

        onView(withId(R.id.editDeadlineTextView)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("TestFacility"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.event_description)).perform(typeText("TestDescription"));

        onView(withId(R.id.savebutton)).perform(click());
        device.findObject(new UiSelector().textContains("Please fill out all fields")).exists();
    }

    /**
     * ESPRESSO TEST
     * Test successful event creation
     */
    @Test
    public void testEventCreationSuccess() throws UiObjectNotFoundException {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());

        onView(withId(R.id.edit_title_button)).perform(click());
        onView(withId(R.id.event_title_edittext)).perform(typeText("TestEventName"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.edit_dtl_button)).perform(click());
        onView(withId(R.id.editDateTextView)).perform(click());
        device.findObject(new UiSelector().text("23")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editDeadlineTextView)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("TestFacility"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.event_description)).perform(typeText("TestDescription"));

        onView(withId(R.id.savebutton)).perform(click());
        device.findObject(new UiSelector().textContains("Event created successfully!")).exists();
    }

    /**
     * ESPRESSO TEST
     * Test successful event creation
     */
    @Test
    public void testEventViewing() throws UiObjectNotFoundException {
        onView(withId(R.id.organizer_events_button)).perform(click());
        onView(withId(R.id.add_button_bottom)).perform(click());

        onView(withId(R.id.edit_title_button)).perform(click());
        onView(withId(R.id.event_title_edittext)).perform(typeText("TestEventName"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.edit_dtl_button)).perform(click());

        onView(withId(R.id.editDateTextView)).perform(click());
        device.findObject(new UiSelector().text("25")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editDeadlineTextView)).perform(click());
        device.findObject(new UiSelector().text("20")).click();
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("TestFacility"));
        onView(withText("Confirm")).perform(click());

        onView(withId(R.id.event_description)).perform(typeText("TestDescription"));

        onView(withId(R.id.savebutton)).perform(click());

        onView(withText("View")).perform(click());
        onView(withId(R.id.event_qr_generate_button)).check(matches(isDisplayed()));
    }
}