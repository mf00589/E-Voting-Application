package com.example.e_voting;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminTest {

    @Rule
    public IntentsTestRule<Admin> intentsTestRule =
            new IntentsTestRule<>(Admin.class);


    @Test
    public void avotecount() throws Exception{
        onView(withId(R.id.vote_count)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(VoteCounting.class.getName()));
        Thread.sleep(2000L);

    }



    @Test
    public void adduser() throws  Exception{
        onView(withId(R.id.addUser)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(AddUser.class.getName()));
        Thread.sleep(2000L);


    }

    @Test
    public void logout() throws  Exception{
        onView(withId(R.id.button4)).perform(click());
        Thread.sleep(2000L);
        intended(hasComponent(Login.class.getName()));
        Thread.sleep(2000L);



    }

}
