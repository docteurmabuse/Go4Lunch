package com.tizzone.go4lunch;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends TestCase {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
    private MainActivity activity;

    @Before
    public void init() {
        activity = rule.getActivity();
    }

    //Test Fragment are showing
    @Test
    public void checkIfMapFragmentIsShowingOnMapMenuButtonClick() {
        // Verify that performing a click the view visibility matches display state
        onView(withId(R.id.navigation_map)).perform(click());
        onView(withId(R.id.navigation_map)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfListViewFragmentIsShowingOnListMenuButtonClick() {
        // Verify that performing a click the view visibility matches display state
        onView(withId(R.id.navigation_list)).perform(click());
        onView(withId(R.id.navigation_list)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfWorkmatesFragmentIsShowingOnWorkmatesMenuButtonClick() {
        // Verify that performing a click the view visibility matches display state
        onView(withId(R.id.navigation_workmates)).perform(click());
        onView(withId(R.id.navigation_workmates)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfSettingActivityIsShowingOnSettingsMenuButtonClick() {
        // Verify that performing a click the view visibility matches display state
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))).perform(DrawerActions.open());
        onView(withId(R.id.nav_settings)).perform(click());
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfAuthActivityIsShowingOnMenuButtonClick() {
        // Verify that performing a click on logout button in drawer show auth activity with Google Sign In button
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))).perform(DrawerActions.open());
        onView(withId(R.id.nav_logout)).perform(click());
        onView(withId(R.id.google_signin)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfPlaceDetailActivityIsShowingOnMenuButtonClick() {
        // Verify that performing a click on logout button in drawer show auth activity with Google Sign In button
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))).perform(DrawerActions.open());
        onView(withId(R.id.nav_lunch)).perform(click());
        onView(withId(R.id.detail_activity_layout)).check(matches(isDisplayed()));
    }
}