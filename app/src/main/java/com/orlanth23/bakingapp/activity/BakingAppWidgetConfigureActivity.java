package com.orlanth23.bakingapp.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.orlanth23.bakingapp.BakingAppWidget;
import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.adapter.SpinnerAdapter;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.provider.ProviderUtilities;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link BakingAppWidget BakingAppWidget} AppWidget.
 */
public class BakingAppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.orlanth23.bakingapp.BakingAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Spinner mAppWidgetSpinner;
    private long mRecipeId = -1;
    // Click on the Add button
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = BakingAppWidgetConfigureActivity.this;

            if (mRecipeId != -1) {
                // When the button is clicked, store the recipe id locally
                saveRecipeIdPref(context, mAppWidgetId, mRecipeId);

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                BakingAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        }
    };

    // On spinner select item, catch the recipe id
    AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mRecipeId = id;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mRecipeId = -1;
        }
    };

    public BakingAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipeIdPref(Context context, int appWidgetId, long recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, recipeId);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static long loadRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long recipeId = prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1);
        if (recipeId != -1) {
            return recipeId;
        } else {
            return -1;
        }
    }

    public static void deleteRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_app_widget_configure);
        mAppWidgetSpinner = findViewById(R.id.appwidget_spinner);
        mAppWidgetSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        // Get List of recipe from ContentProvider
        ArrayList<Recipe> listRecipe = ProviderUtilities.getListRecipeFromContentProvider(getApplicationContext());

        // Create a new adapter for the spinner
        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.drawer_spinner_recipe, listRecipe);
        mAppWidgetSpinner.setAdapter(adapter);

        // Get the button view and set an OnClickListener
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // Get the recipe id from the shared prefs
        mRecipeId = loadRecipeIdPref(BakingAppWidgetConfigureActivity.this, mAppWidgetId);

        // Get the position of the recipe in the recipe list
        int position = -1;
        for (Recipe recipe : listRecipe){
            if (recipe.getId() == mRecipeId){
                position = listRecipe.indexOf(recipe);
                break;
            }
        }

        // Change the spinner position with the position
        mAppWidgetSpinner.setSelection(position);
    }
}