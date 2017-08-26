package com.orlanth23.bakingapp.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orlanth23.bakingapp.Constants;
import com.orlanth23.bakingapp.IdlingResource.SimpleIdlingResource;
import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.adapter.RecipeAdapter;
import com.orlanth23.bakingapp.broadcast.NetworkReceiver;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.provider.ProviderUtilities;
import com.orlanth23.bakingapp.task.GetRecipesFromNetwork;
import com.orlanth23.bakingapp.task.RecipeListTaskListener;

import java.util.ArrayList;

public class RecipeListActivity extends AppCompatActivity implements NetworkReceiver.NetworkChangeListener, UpdateViewActivityInterface<ArrayList<Recipe>> {

    private static final String TAG = RecipeListActivity.class.getName();
    private static final int NUMBER_GRID_COLUMN = 3;

    private Context mContext = this;

    private RecyclerView mRecyclerView;
    private FloatingActionButton mRefreshButton;
    private NetworkReceiver mNetworkReceiver;
    private RecipeListTaskListener mRecipeListTaskListener;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
            mIdlingResource.setIdleState(false);
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIdlingResource();

        // Create a new listener
        mRecipeListTaskListener = new RecipeListTaskListener(this);

        // Register a network broadcast receiver to apply modification when network connectivity change
        mNetworkReceiver = new NetworkReceiver(this);
        registerReceiver(mNetworkReceiver, NetworkReceiver.CONNECTIVITY_CHANGE_INTENT_FILTER);

        setContentView(R.layout.activity_recipe_list);

        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        mRefreshButton = (FloatingActionButton) findViewById(R.id.refresh_button);

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshButton.setVisibility(View.GONE);
                getRecipeListFromTheNet();
            }
        });

        textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.DANCING_FONT));
        textView.setText(getTitle());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUMBER_GRID_COLUMN);
        mRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Get Recipe List from contentProvider
        ArrayList<Recipe> recipeList = ProviderUtilities.getListRecipeFromContentProvider(this);
        if (recipeList.size() > 0) {
            setupRecyclerView(mRecyclerView, recipeList);
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        } else {
            if (mNetworkReceiver.checkConnection(this)) {
                getRecipeListFromTheNet();
            } else {
                Toast.makeText(this, R.string.no_connection_to_get_recipe_list, Toast.LENGTH_LONG).show();
            }
        }

        assert mRecyclerView != null;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<Recipe> recipeArrayList) {
        recyclerView.setAdapter(new RecipeAdapter(recipeArrayList));
    }

    private void getRecipeListFromTheNet() {
        new GetRecipesFromNetwork(this, mRecipeListTaskListener).execute();
    }

    @Override
    public void OnNetworkEnable() {
        mRefreshButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnNetworkDisable() {
        mRefreshButton.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public void onCompleteUpdateView(final ArrayList<Recipe> result) {
        setupRecyclerView(mRecyclerView, result);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // When we get the recipe list from the network, we populate our content provider with.
                ProviderUtilities.populateContentProviderFromList(mContext, result);

                // Only use for test
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }
                Snackbar.make(mRecyclerView, R.string.snackbar_recipe_list_uptodate, Snackbar.LENGTH_LONG).show();
                mNetworkReceiver.checkConnection(mContext);
            }
        };
        runnable.run();
    }

    @Override
    public void onFailureUpdateView(Exception e) {
        Log.e(TAG, e.getMessage(), e);
        Snackbar.make(mRecyclerView, getString(R.string.error_while_getting_recipe_list), Snackbar.LENGTH_LONG).show();
    }
}
