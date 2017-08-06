package com.orlanth23.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.orlanth23.bakingapp.activity.RecipeListActivity;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.provider.ProviderUtilities;
import com.orlanth23.bakingapp.service.ListViewService;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakingAppWidgetConfigureActivity BakingAppWidgetConfigureActivity}
 */
public class BakingAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Get the recipe id from the shared preferences
        long recipeId = BakingAppWidgetConfigureActivity.loadRecipeIdPref(context, appWidgetId);
        Recipe recipe = ProviderUtilities.getRecipeByRecipeId(context, recipeId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);

        if (recipe != null) {
            views.setTextViewText(R.id.appwidget_recipe_name, recipe.getName());
        }

        // Create a intent to act as the adapter for the listView
        Intent intentListViewService = new Intent(context, ListViewService.class);
        intentListViewService.setData(Uri.fromParts("content", String.valueOf(recipeId), null));
        views.setRemoteAdapter(R.id.appwidget_listview, intentListViewService);

        // Construct a pending intent to the main activity
        Intent intentRecipeListActivity = new Intent(context, RecipeListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentRecipeListActivity, 0);

        views.setOnClickPendingIntent(R.id.linear_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_listview);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BakingAppWidgetConfigureActivity.deleteRecipeIdPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

