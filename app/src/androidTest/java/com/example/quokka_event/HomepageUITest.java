package com.example.quokka_event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.not;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomepageUITest {
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
     * Check that the app requests location permissions on startup.
     */
    @Test
    public void aTestPermissions() throws UiObjectNotFoundException {
        device.findObject(new UiSelector().text("While using the app")).click();
        onView(withId(R.id.my_events_button)).check(matches(isDisplayed()));
    }

    /**
     * ESPRESSO TEST
     * Check that the admin button visibility changes based on isAdmin flag.
     */
    @Test
    public void bTestAdminButtonVisibility() {
        scenario.getScenario().onActivity(activity -> activity.showAdminButton(true));
        onView(withId(R.id.admin_button)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_button)).check(matches(not(isDisplayed())));
    }

    /**
     * ESPRESSO TEST
     * Check that the Scan QR Code button triggers a camera permission request.
     */
    @Test
    public void cTestCameraPermissions() throws UiObjectNotFoundException {
        onView(withId(R.id.scan_qr_button)).perform(click());
        device.findObject(new UiSelector().text("Only this time")).click();
    }

    /**
     * ESPRESSO TEST
     * Check that the activity switches to the Notification Page when the bell icon is pressed.
     */
    @Test
    public void dTestNavigateToNotificationPage() {
        Intents.init();
        onView(withId(R.id.bell)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(NotificationPageActivity.class.getName()));
        Intents.release();
    }

    /**
     * ESPRESSO TEST
     * Check that tapping the profile icon navigates to the User Profile page.
     */
    @Test
    public void E_testNavigateToUserProfile() {
        Intents.init();
        onView(withId(R.id.profile)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(UserProfilePageActivity.class.getName()));
        Intents.release();
    }

    /**
     * ESPRESSO TEST
     * Check that tapping the Organizer Events button navigates to the Organizer Events page.
     */
    @Test
    public void F_testNavigateToOrganizerEvents() {
        Intents.init();
        onView(withId(R.id.organizer_events_button)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(OrganizerEventsPageActivity.class.getName()));
        Intents.release();
    }
}