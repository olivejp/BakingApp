package com.orlanth23.bakingapp.service;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.domain.Ingredient;
import com.orlanth23.bakingapp.provider.ProviderUtilities;

import java.util.ArrayList;

/**
 * Created by orlanth23 on 05/08/2017.
 */

public class ListViewService extends RemoteViewsService {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        long recipeId = intent.getLongExtra(EXTRA_RECIPE_ID, -1);
        return new ListViewsFactory(this.getApplicationContext(), recipeId);
    }
}

class ListViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    long recipeId;
    ArrayList<Ingredient> ingredientList;

    public ListViewsFactory(Context context, long recipeId) {
        this.context = context;
        this.recipeId = recipeId;
        this.ingredientList = null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Get the list of ingredient for this recipe from the content provider
        ingredientList = ProviderUtilities.getIngredientListByRecipeId(context, recipeId);
    }

    @Override
    public void onDestroy() {
        ingredientList.clear();
    }

    @Override
    public int getCount() {
        return ingredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (ingredientList.isEmpty()){
            return null;
        }

        Ingredient ingredient = ingredientList.get(i);

        // Create a new remote views
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_list_content_widget);

        // Set the value of the ingredient into the views
        views.setTextViewText(R.id.ingredient_name, ingredient.getIngredient());
        views.setTextViewText(R.id.ingredient_measure_quantity, ingredient.getMeasureQuantity());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return ingredientList.indexOf(ingredientList.get(i));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}