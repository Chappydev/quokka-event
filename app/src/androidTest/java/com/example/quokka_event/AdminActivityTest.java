package com.example.quokka_event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.quokka_event.controllers.AdminBrowseEventsActivity;
import com.example.quokka_event.controllers.AdminFacilityActivity;
import com.example.quokka_event.controllers.AdminLandingPageActivity;
import com.example.quokka_event.controllers.BrowseProfilesActivity;

import org.junit.Rule;
import org.junit.Test;

public class AdminActivityTest {
    @Rule
    public ActivityScenarioRule<AdminLandingPageActivity> scenario = new ActivityScenarioRule<>(AdminLandingPageActivity.class);

    @Test
    public void testBrowseEvents(){
        Intents.init();

        onView(withId(R.id.browse_events_button)).perform(click());
        intended(hasComponent(AdminBrowseEventsActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testBrowseProfiles(){
        Intents.init();

        onView(withId(R.id.browse_profile_button)).perform(click());
        intended(hasComponent(BrowseProfilesActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testBrowseFacilities(){
        Intents.init();

        onView(withId(R.id.admin_browse_facility)).perform(click());
        intended(hasComponent(AdminFacilityActivity.class.getName()));
        Intents.release();
    }

}
