package com.example.e_voting;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<Login> intentsTestRule =
           new IntentsTestRule<>(Login.class);


    private String text = "E-Voting";

    @Test
    public void clickLoginButton_ShowsSnackBarRightCredentials() throws Exception {

        onView(withId(R.id.email)).perform(typeText("ades@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("1234567890"));
        onView(withId(R.id.buttonlogin)).perform(click());

       // Intents.init();
        Thread.sleep(2000L);

        intended(hasComponent(PhoneVerification.class.getName()));
        Thread.sleep(2000L);


    }

    @Test
    public void clickRegister() throws Exception{
        onView(withId(R.id.registerCreate2)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(Register.class.getName()));

    }

    @Test
    public void loginAdmin() throws Exception {

        onView(withId(R.id.email)).perform(typeText("admin4@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("1234567890"));
        onView(withId(R.id.buttonlogin)).perform(click());

        // Intents.init();
        Thread.sleep(2000L);

        intended(hasComponent(Admin.class.getName()));
        Thread.sleep(2000L);


    }


}

