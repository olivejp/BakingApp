package com.orlanth23.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orlanth23.bakingapp.activity.RecipeDetailActivity;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.provider.ProviderUtilities;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by orlanth23 on 17/07/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeDetailActivityTest {

    private Context mContext = InstrumentationRegistry.getTargetContext();

    // Because RecipteDetailActivity needs an Intent, I made a custom ActivityTestRule and override the getIntent method to pass an custom test Intent to the activity.
    private CustomActivityTestRule.getIntentListener getIntentListener = new CustomActivityTestRule.getIntentListener() {
        @Override
        public Intent getIntent() {
            // Populated the ContentProvider with some fresh recipes from JSON Assets (we don't want to get the list from internet for the test)
            String json = Utilities.loadJSONFromAsset(mContext, "recipe_list.json");
            Type listType = new TypeToken<ArrayList<Recipe>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Recipe> mArrayList = gson.fromJson(json, listType);
            ProviderUtilities.populateContentProviderFromList(mContext, mArrayList);

            // Create an intent and return it
            Intent intent = new Intent();
            if (mArrayList != null) {
                intent.putExtra(RecipeDetailActivity.ARG_RECIPE, mArrayList.get(1));
            }
            return intent;
        }
    };

    @Rule
    public CustomActivityTestRule<RecipeDetailActivity> recipeDetailActivityTestRule = new CustomActivityTestRule<>(RecipeDetailActivity.class, null, getIntentListener);

    public void checkClickOn() {
        // We check that the frame for the recipe detail is present
        onView(withId(R.id.frame_detail_recipe)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Check that the recyclerview of the ingredients is displayed
        onView(withId(R.id.recipe_detail_list_ingredients)).check(matches(isDisplayed()));

        // We double swipe up to the recipe's steps list
        onView(withId(R.id.recipe_detail_container)).perform(swipeUp()).perform(swipeUp());

        // Check that the recyclerview of the steps is displayed
        onView(withId(R.id.recipe_detail_list_steps)).check(matches(isDisplayed()));

        // we click on the first element of the step list
        onView(withId(R.id.recipe_detail_list_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // we check that the step description is displayed
        onView(withId(R.id.step_description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void clickOnPortrait() throws Exception {
        // Change the orientation
        recipeDetailActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkClickOn();
    }

    @Test
    public void clickOnLandscape() throws Exception {
        // Change the orientation
        recipeDetailActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        checkClickOn();
    }
}

