package com.orlanth23.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.orlanth23.bakingapp.activity.RecipeDetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by orlanth23 on 17/07/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeDetailActivityTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> recipeDetailactivityTestRule = new ActivityTestRule<>(RecipeDetailActivity.class);

    @Test
    public void clickOn() throws Exception {
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Checks that the OrderActivity opens with the correct tea name displayed
        onView(withId(R.id.frame_detail_recipe)).check(matches(isDisplayed()));
    }
}

