package com.example.quokka_event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class) @LargeTest

public class ProfileEditingTest {
    @Before
    public void disableAnimations() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            device.executeShellCommand("settings put global window_animation_scale 0");
            device.executeShellCommand("settings put global transition_animation_scale 0");
            device.executeShellCommand("settings put global animator_duration_scale 0");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private View decorView;
    /**
     * Set up decorView for checking for toasts
     * Credit: https://stackoverflow.com/a/54127456 Authors: Akbolat SSS, Gk Mohammad Emon
     */
    @Before
    public void setUp() {
        scenario.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);


    /**
     * ESPRESSO TEST
     * Check that the activity switches to the profile edit view when the profile button is pressed
     *
     * @author chappydev
     * @since project part 4
     */
    @Test
    public void testActivity() {

        onView(withId(R.id.profile)).perform(click());

        // Check we're on the correct activity
        onView(withId(R.id.userEditProfile)).check(matches(isDisplayed()));
    }

    @Test
    public void testInputValidation() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.profile)).perform(click());

        // Check we're on the correct activity
        onView(withId(R.id.userEditProfile)).check(matches(isDisplayed()));

        onView(withId(R.id.user_name_field)).perform(click()).perform(typeText("Input Validator Guy"), closeSoftKeyboard());

        onView(withId(R.id.save_changes_button)).perform(scrollTo()).perform(click());

        Thread.sleep(700);
        onView(withText("Please fill in all profile fields!")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));
        Thread.sleep(2500);

        onView(withId(R.id.user_email)).perform(scrollTo()).perform(click()).perform(typeText("notavalidemail.com"), closeSoftKeyboard());
        Thread.sleep(1000);

        onView(withId(R.id.save_changes_button)).perform(scrollTo()).perform(click());

        Thread.sleep(1000);
        onView(withText("Please fill in all profile fields!")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));
        Thread.sleep(2000);

        onView(withId(R.id.user_email)).perform(scrollTo()).perform(click()).perform(replaceText("valid@email.com"), closeSoftKeyboard());

        onView(withId(R.id.save_changes_button)).perform(scrollTo()).perform(click());

        Thread.sleep(1000);
        onView(withText("Please fill in all profile fields!")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(Matchers.not(isDisplayed())));
        Thread.sleep(2000);
    }
}
