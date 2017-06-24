package com.orlanth23.bakingapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.orlanth23.bakingapp.IdlingResource.SimpleIdlingResource;
import com.orlanth23.bakingapp.adapter.RecipeAdapter;
import com.orlanth23.bakingapp.domain.ListRecipe;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.network.JsonDownloader;
import com.orlanth23.bakingapp.network.NetworkUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link recipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class recipeListActivity extends AppCompatActivity implements JsonDownloader.DelayerCallback {

    private static final String TAG = recipeListActivity.class.getName();
    public static final String API_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private boolean mTwoPane;
    private Context mContext = this;
    private ListRecipe mListRecipes = ListRecipe.getInstance();
    private RecyclerView mRecyclerView;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        assert mRecyclerView != null;

        // Are we in two panel mode
        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
        }

        // Call network to get recipe list
        if (!mListRecipes.hasBeenLoaded()) {
            new GetRecipesFromNetwork().execute();
        } else {
            setupRecyclerView(mRecyclerView);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecipeAdapter(this, mListRecipes.getListRecipe(), mTwoPane));

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    public void onDone(ArrayList<Recipe> listRecipes) {

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
                    mListRecipes.setListRecipe(tempList);
                    mListRecipes.setHasBeenLoaded(true);
                    setupRecyclerView(mRecyclerView);
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(mContext, "Le chargement des recettes n'a pas fonctionné.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
