package com.orlanth23.bakingapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orlanth23.bakingapp.provider.RecipesProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by orlanth23 on 01/07/2017.
 */

@RunWith(AndroidJUnit4.class)
public class ContentProviderTest {

    private Context mContext;

    private TestUtilities.TestContentObserver tco;

    private void deleteRecords(Uri uri) {
        mContext.getContentResolver().delete(
                uri,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            assertEquals("Error: Records from URI " + uri.toString() + " not deleted table during delete", 0, cursor.getCount());
            cursor.close();
        } else {
            assertTrue(false);
        }
    }

    private void testInsertReadUriProvider(Uri uri, ContentValues contentValues) {

        /* Suppression des enregistrements précédents */
        deleteRecords(uri);

        /* add a ContentObserver */
        mContext.getContentResolver().registerContentObserver(uri, true, tco);

        /* try to insert */
        Uri categorieUri = mContext.getContentResolver().insert(uri, contentValues);

        /* verify that the notifyChange has been called */
        tco.waitForNotificationOrFail();

        /* unregister the contentObserver */
        mContext.getContentResolver().unregisterContentObserver(tco);

        long rowId = ContentUris.parseId(categorieUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating URI ".concat(uri.toString()),
                cursor, contentValues);
    }

    @Before
    public void precondition() {
        mContext = InstrumentationRegistry.getTargetContext();
        tco = TestUtilities.getTestContentObserver();
    }


    @Test
    public void deleteAllRecordsFromRecipeProvider() {
        deleteRecords(RecipesProvider.ListRecipe.LIST_RECIPE);
        deleteRecords(RecipesProvider.ListStep.LIST_STEP);
        deleteRecords(RecipesProvider.ListIngredient.LIST_INGREDIENT);
    }

    @Test
    public void deleteAllRecordsFromIngredientProvider() {
        deleteRecords(RecipesProvider.ListIngredient.LIST_INGREDIENT);
    }

    @Test
    public void testInsertReadStepProvider() {
        ContentValues testValuesStep = TestUtilities.createStepValues(1, 1234);
        testInsertReadUriProvider(RecipesProvider.ListStep.LIST_STEP, testValuesStep);
    }

    @Test
    public void testInsertReadIngredientProvider() {
        ContentValues testValuesIngredient = TestUtilities.createIngredientValues(1, 1234);
        testInsertReadUriProvider(RecipesProvider.ListIngredient.LIST_INGREDIENT, testValuesIngredient);
    }

    @Test
    public void testInsertReadRecipeWithNullIdProvider() {
        ContentValues testValuesAnnonceNull = TestUtilities.createRecipeValuesNullId();
        testInsertReadUriProvider(RecipesProvider.ListRecipe.LIST_RECIPE, testValuesAnnonceNull);
    }

    @Test
    public void testInsertReadRecipeProvider() {
        ContentValues testValuesAnnonce = TestUtilities.createRecipeValues(1234);
        testInsertReadUriProvider(RecipesProvider.ListRecipe.LIST_RECIPE, testValuesAnnonce);
    }
}
