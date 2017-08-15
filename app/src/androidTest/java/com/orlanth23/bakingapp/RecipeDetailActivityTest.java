package com.orlanth23.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orlanth23.bakingapp.activity.RecipeDetailActivity;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.provider.ProviderUtilities;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

    private Context mContext;

    private CustomActivityTestRule.onBeforeListener onBeforeListener = new CustomActivityTestRule.onBeforeListener() {
        @Override
        public void onBefore() {
            // Populated the

        }
    };

    private CustomActivityTestRule.getIntentListener getIntentListener = new CustomActivityTestRule.getIntentListener() {
        @Override
        public Intent getIntent() {
            // Create an intent
            Intent intent = new Intent();
            return  intent;
        }
    };

    @Rule
    public CustomActivityTestRule<RecipeDetailActivity> recipeDetailActivityTestRule = new CustomActivityTestRule<>(RecipeDetailActivity.class);

    @Before
    public void precondition() {
        mContext = InstrumentationRegistry.getTargetContext();
        String json = Utilities.loadJSONFromAsset(mContext, "recipe_list.json");

        if (json != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Recipe>>() {
            }.getType();
            final ArrayList<Recipe> tempList = gson.fromJson(json, listType);

            // When we get the recipe list from the network, we populate our content provider with.
            ProviderUtilities.populateContentProviderFromList(mContext, tempList);
        }
    }


    @Test
    public void clickOn() throws Exception {
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.frame_detail_recipe)).check(matches(isDisplayed()));
    }
}

