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

    private Context mContext = InstrumentationRegistry.getTargetContext();
    private ArrayList<Recipe> mArrayList;

    private CustomActivityTestRule.getIntentListener getIntentListener = new CustomActivityTestRule.getIntentListener() {
        @Override
        public Intent getIntent() {
            // Populated the ContentProvider with some fresh recipes from JSON Assets
            String json = Utilities.loadJSONFromAsset(mContext, "recipe_list.json");
            Type listType = new TypeToken<ArrayList<Recipe>>() {
            }.getType();
            Gson gson = new Gson();
            mArrayList = gson.fromJson(json, listType);
            ProviderUtilities.populateContentProviderFromList(mContext, mArrayList);

            // Create an intent
            Intent intent = new Intent();
            intent.putExtra(RecipeDetailActivity.ARG_RECIPE, mArrayList.get(1));
            return intent;
        }
    };

    @Rule
    public CustomActivityTestRule<RecipeDetailActivity> recipeDetailActivityTestRule = new CustomActivityTestRule<>(RecipeDetailActivity.class, null, getIntentListener);

    @Test
    public void clickOn() throws Exception {
        onView(withId(R.id.frame_detail_recipe)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_detail_list_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.step_description)).check(matches(isDisplayed()));
    }
}

