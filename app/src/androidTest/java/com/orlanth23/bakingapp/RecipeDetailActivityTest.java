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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
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

            // Okay we 've got the list in a json let's just transform to an arrayList and send it to the content provider.
            ArrayList<Recipe> mRecipeList = gson.fromJson(json, listType);
            ProviderUtilities.populateContentProviderFromList(mContext, mRecipeList);

            // Create an intent with the first recipe of the list send it to the activity
            Intent intent = new Intent();
            if (mRecipeList != null) {
                intent.putExtra(RecipeDetailActivity.ARG_RECIPE, mRecipeList.get(1));
            }
            return intent;
        }
    };

    @Rule
    public CustomActivityTestRule<RecipeDetailActivity> recipeDetailActivityTestRule = new CustomActivityTestRule<>(RecipeDetailActivity.class, null, getIntentListener);

    public void clickOnFirstStep(int orientation) {
        // Change the orientation
        recipeDetailActivityTestRule.getActivity().setRequestedOrientation(orientation);

        // We check that the frame for the recipe detail is present
        onView(withId(R.id.frame_detail_recipe)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Check that the recyclerview of the ingredients is displayed
        onView(withId(R.id.recipe_detail_list_ingredients)).check(matches(isDisplayed()));

        // We swipe up to the recipe's steps list
        onView(withId(R.id.recipe_detail_container)).perform(swipeUp());

        // Check that the recyclerview of the steps is displayed
        onView(withId(R.id.recipe_detail_list_steps)).check(matches(isDisplayed()));

        // we click on the first element of the step list
        onView(withId(R.id.recipe_detail_list_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (TestUtilities.isTablet(recipeDetailActivityTestRule.getActivity())) {
                // we check that the step description is displayed (only available on tablet)
                onView(withId(R.id.step_description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            } else {
                onView(withId(R.id.player_view)).check(matches(isDisplayed()));
            }
        } else {
            onView(withId(R.id.step_description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }

    @Test
    public void clickOnFirstStepPortrait() throws Exception {
        // Change the orientation
        clickOnFirstStep(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Test
    public void clickOnFirstStepLandscape() throws Exception {
        // Change the orientation
        clickOnFirstStep(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Test
    public void rotationPortraitToLandscape() throws Exception {
        // Change the orientation
        recipeDetailActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // We check that the frame for the recipe detail is present
        onView(withId(R.id.frame_detail_recipe)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Change the orientation
        recipeDetailActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // We check that the frame for the recipe detail is present
        onView(withId(R.id.frame_detail_recipe)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void checkRotationMasterDetailFlowOnTablet() throws Exception {
        if (TestUtilities.isTablet(recipeDetailActivityTestRule.getActivity())) {
            // Click on the first step
            clickOnFirstStep(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Change the orientation
            recipeDetailActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // we check that the player is displayed
            onView(withId(R.id.player_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        } else {
            Assert.assertTrue(true);
        }
    }
}

