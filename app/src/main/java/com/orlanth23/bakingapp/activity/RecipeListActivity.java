package com.orlanth23.bakingapp.activity;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.orlanth23.bakingapp.IdlingResource.SimpleIdlingResource;
import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.adapter.RecipeAdapter;
import com.orlanth23.bakingapp.domain.Ingredient;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.domain.Step;
import com.orlanth23.bakingapp.network.NetworkUtils;
import com.orlanth23.bakingapp.provider.ProviderUtilities;
import com.orlanth23.bakingapp.provider.RecipesProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RecipeListActivity extends AppCompatActivity {

    private static final String TAG = RecipeListActivity.class.getName();
    public static final String API_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private Context mContext = this;
    private RecyclerView mRecyclerView;
    private boolean isConnected;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        mRecyclerView.setLayoutManager(gridLayoutManager);


        assert mRecyclerView != null;

        // Call network to get recipe list
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            new GetRecipesFromNetwork().execute();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        ArrayList<Recipe> recipeList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(RecipesProvider.ListRecipe.LIST_RECIPE, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Recipe recipe = ProviderUtilities.getRecipeFromCursor(cursor);

                Cursor cursorStep = getContentResolver().query(RecipesProvider.ListStep.withRecipeId(recipe.getId()), null, null, null, null);
                if (cursorStep != null) {
                    while (cursorStep.moveToNext()) {
                        Step step = ProviderUtilities.getStepFromCursor(cursorStep);
                        recipe.getSteps().add(step);
                    }
                    cursorStep.close();
                }

                Cursor cursorIngredient = getContentResolver().query(RecipesProvider.ListIngredient.withRecipeId(recipe.getId()), null, null, null, null);
                if (cursorIngredient != null) {
                    while (cursorIngredient.moveToNext()) {
                        Ingredient ingredient = ProviderUtilities.getIngredientFromCursor(cursorIngredient);
                        recipe.getIngredients().add(ingredient);
                    }
                    cursorIngredient.close();
                }

                recipeList.add(recipe);
            }
            cursor.close();
        }

        recyclerView.setAdapter(new RecipeAdapter(recipeList));

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
            mIdlingResource.setIdleState(false);
        }
        return mIdlingResource;
    }

    private class GetRecipesFromNetwork extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return NetworkUtils.makeServiceCall(API_URL);
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            if (json != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Recipe>>() {
                }.getType();
                try {
                    ArrayList<Recipe> tempList = gson.fromJson(json, listType);
                    ProviderUtilities.populateContentProviderFromJson(mContext, tempList);
                    setupRecyclerView(mRecyclerView);
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(mContext, "Le chargement des recettes n'a pas fonctionné.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
