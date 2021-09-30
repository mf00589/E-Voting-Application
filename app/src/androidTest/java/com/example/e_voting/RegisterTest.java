package com.example.e_voting;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RegisterTest {

    @Rule
    public IntentsTestRule<Register> intentsTestRule =
            new IntentsTestRule<>(Register.class);


    @Test
    public void aregisteration() throws Exception {

        onView(withId(R.id.name)).perform(typeText("espresso2"));
        onView(withId(R.id.email)).perform(typeText("espresso2@gmail.com"));
        onView(withId(R.id.password)).perform((typeText("1234567890")));
        onView(withId(R.id.phone)).perform((typeText("9876543210")),closeSoftKeyboard());
        Thread.sleep(2000L);

        onView(withId(R.id.register)).perform(click());

        // Intents.init();
        Thread.sleep(2000L);

        intended(hasComponent(MainActivity.class.getName()));
        Thread.sleep(2000L);


    }
    @Test
    public void bregister() throws Exception{
        onView(withId(R.id.registerCreate2)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(Login.class.getName()));

    }

}
