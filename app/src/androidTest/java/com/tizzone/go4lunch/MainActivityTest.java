package com.tizzone.go4lunch;

import android.view.Gravity;
import android.view.View;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.tizzone.go4lunch.databinding.ActivityMainBinding;
import com.tizzone.go4lunch.databinding.NavHeaderMainBinding;
import com.tizzone.go4lunch.models.User;

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
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends TestCase {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
    private NavHeaderMainBinding navHeaderMainBinding;
    private ActivityMainBinding mBinding;
    private View headerView;
    private FirebaseUser firebaseUser;
    private User user;
    private MainActivity activity;

    public void setUp() throws Exception {
        super.setUp();
        activity = rule.getActivity();
        mBinding = ActivityMainBinding.inflate(activity.getLayoutInflater());
        headerView = mBinding.drawerNavView.getHeaderView(0);
        navHeaderMainBinding = NavHeaderMainBinding.bind(headerView);
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_nav_view);
        user = new User("987654", "dario.argento@gmail.com", "DarioArgento", "https://fr.wikipedia.org/wiki/Dario_Argento#/media/Fichier:Dario_Argento_at_the_Brussels_International_Fantastic_Film_Festival_in_2007.jpg", null, "123456", "Les Trois Violons");
        navHeaderMainBinding.setUser(user);
    }

    @Before
    public void init() {
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


    @Test
    public void checkIfUserInfoAreDisplayedInDrawer() {
        // Verify that user info are displaying correctly in drawer
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))).perform(DrawerActions.open());
        onView(withId(R.id.profileName)).check(matches(withText("DarioArgento")));

    }
}