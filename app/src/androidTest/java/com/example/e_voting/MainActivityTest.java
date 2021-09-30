package com.example.e_voting;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Test
    public void averify() throws Exception{
        onView(withId(R.id.verify)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(VerifyUser.class.getName()));
        Thread.sleep(2000L);

    }
    @Test
    public void avoting() throws Exception{
        onView(withId(R.id.logout)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(Voting.class.getName()));
        Thread.sleep(2000L);

    }
    @Test
    public void logoff() throws Exception{
        onView(withId(R.id.signoff)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(Login.class.getName()));
        Thread.sleep(2000L);

    }

}
