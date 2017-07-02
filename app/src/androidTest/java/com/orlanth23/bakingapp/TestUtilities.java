package com.orlanth23.bakingapp;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

import com.orlanth23.bakingapp.provider.IngredientInterface;
import com.orlanth23.bakingapp.provider.RecipeInterface;
import com.orlanth23.bakingapp.provider.StepInterface;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TestUtilities {

    public static final String STEP_DESRIPTION = "description";
    public static final String STEP_SHORT_DESRIPTION = "shortDescription";
    public static final String STEP_VIDEO_URL = "videoUrl";
    public static final String STEP_THUMBNAIL_URL = "thumbnailUrl";


    public static final String RECIPE_IMAGE = "recipeImage";
    public static final String RECIPE_NAME = "recipeName";
    public static final String RECIPE_SERVINGS = "recipeServings";

    public static final String INGREDIENT_INGREDIENT = "ingredient";
    public static final String INGREDIENT_MEASURE = "measure";
    public static final double INGREDIENT_QUANTITY = 2.5;


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createStepValues(Integer idRecipe, @Nullable Integer idStep) {
        ContentValues testValues = new ContentValues();
        if (idStep != null) {
            testValues.put(StepInterface._ID, idStep);
        }
        testValues.put(StepInterface.description, STEP_DESRIPTION);
        testValues.put(StepInterface.shortDescription, STEP_SHORT_DESRIPTION);
        testValues.put(StepInterface.videoURL, STEP_VIDEO_URL);
        testValues.put(StepInterface.thumbnailURL, STEP_THUMBNAIL_URL);
        testValues.put(StepInterface.RECIPE_ID, idRecipe);
        return testValues;
    }

    static ContentValues createIngredientValues(Integer idRecipe, @Nullable Integer idIngredient) {
        ContentValues testValues = new ContentValues();
        if (idIngredient != null) {
            testValues.put(IngredientInterface._ID, idIngredient);
        }
        testValues.put(IngredientInterface.ingredient, INGREDIENT_INGREDIENT);
        testValues.put(IngredientInterface.measure, INGREDIENT_MEASURE);
        testValues.put(IngredientInterface.quantity, INGREDIENT_QUANTITY);
        testValues.put(IngredientInterface.RECIPE_ID, idRecipe);
        return testValues;
    }

    static ContentValues createRecipeValuesNullId() {
        ContentValues testValues = new ContentValues();
        testValues.put(RecipeInterface.image, RECIPE_IMAGE);
        testValues.put(RecipeInterface.name, RECIPE_NAME);
        testValues.put(RecipeInterface.servings, RECIPE_SERVINGS);
        return testValues;
    }

    static ContentValues createRecipeValues(long recipeId) {
        ContentValues testValues = new ContentValues();
        testValues.put(RecipeInterface._ID, recipeId);
        testValues.put(RecipeInterface.image, RECIPE_IMAGE);
        testValues.put(RecipeInterface.servings, RECIPE_SERVINGS);
        testValues.put(RecipeInterface.name, RECIPE_NAME);
        return testValues;
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}

