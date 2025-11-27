package com.zybooks.circuitmaker;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testRegistration() {
        onView(withId(R.id.loginEmail)).perform(ViewActions.typeText("test@test.com"));
        onView(withId(R.id.loginPassword)).perform(ViewActions.typeText("test123"));
        onView(withId(R.id.registerButton)).perform(click());
    }

    @Test
    public void testLogin() {
        onView(withId(R.id.loginEmail)).perform(ViewActions.typeText("test@test.com"));
        onView(withId(R.id.loginPassword)).perform(ViewActions.typeText("test123"));
        onView(withId(R.id.loginButton)).perform(click());
    }

    @Test
    public void testEmptyEmailField() {
        onView(withId(R.id.loginPassword)).perform(ViewActions.typeText("test123"));
        onView(withId(R.id.registerButton)).perform(click());
    }

    @Test
    public void testEmptyPasswordField() {
        onView(withId(R.id.loginEmail)).perform(ViewActions.typeText("test@test.com"));
        onView(withId(R.id.loginButton)).perform(click());
    }
}
