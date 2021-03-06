package com.orlanth23.bakingapp.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.orlanth23.bakingapp.Constants;
import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.adapter.StepAdapter;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.fragment.RecipeDetailFragment;
import com.orlanth23.bakingapp.fragment.StepDetailFragment;

public class RecipeDetailActivity extends AppCompatActivity implements  RecipeDetailFragment.GetTwoPaneActivity{

    public static final String ARG_RECIPE = "ARG_RECIPE";
    private static final String TAG_RECIPE = "TAG_RECIPE";
    private static final String TAG_RECIPE_DETAIL_FRAGMENT = "TAG_RECIPE_DETAIL_FRAGMENT";
    public static final String TAG_STEP_DETAIL_FRAGMENT = "TAG_STEP_DETAIL_FRAGMENT";

    private RecipeDetailFragment mRecipeDetailFragment;
    private StepDetailFragment mStepDetailFragment;
    private Recipe mRecipe;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        // Are we in two panel mode
        mTwoPane = findViewById(R.id.frame_step_container) != null;

        getFromSavedInstanceState(savedInstanceState);

        initializeFragments();

        // Show the Up button in the action bar.
        if (findViewById(R.id.toolbar_title) != null) {
            TextView textView = (TextView) findViewById(R.id.toolbar_title);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.DANCING_FONT));
            textView.setText(mRecipe.getName());

            Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            else {
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TAG_RECIPE, mRecipe);
        mRecipeDetailFragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentByTag(TAG_RECIPE_DETAIL_FRAGMENT);
        if (mRecipeDetailFragment != null) {
            getSupportFragmentManager().putFragment(outState, TAG_RECIPE_DETAIL_FRAGMENT, mRecipeDetailFragment);
        }

        mStepDetailFragment = (StepDetailFragment) getSupportFragmentManager().findFragmentByTag(TAG_STEP_DETAIL_FRAGMENT);
        if (mStepDetailFragment != null) {
            getSupportFragmentManager().putFragment(outState, TAG_STEP_DETAIL_FRAGMENT, mStepDetailFragment);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mStepDetailFragment != null) {
            mStepDetailFragment.initializeViews();
        }
    }

    private void getFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(TAG_RECIPE);
            mRecipeDetailFragment = (RecipeDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_RECIPE_DETAIL_FRAGMENT);
            mStepDetailFragment = (StepDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_STEP_DETAIL_FRAGMENT);
        } else {
            mRecipe = getIntent().getParcelableExtra(ARG_RECIPE);
        }
    }

    private void initializeFragments() {
        // Remove the fragments from their old containers
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate(StepAdapter.BACKSTACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_detail_recipe);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        fragment = getSupportFragmentManager().findFragmentById(R.id.frame_step_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getSupportFragmentManager().executePendingTransactions();

        // Initialize RecipeDetailFragment
        if (mRecipeDetailFragment == null) {
            mRecipeDetailFragment = RecipeDetailFragment.newInstance(mRecipe);
        }

        if (mTwoPane || mStepDetailFragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_detail_recipe, mRecipeDetailFragment, TAG_RECIPE_DETAIL_FRAGMENT)
                    .commit();
        }

        // Initialize StepDetailFragment
        if (mStepDetailFragment != null) {
            if (mTwoPane) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_step_container, mStepDetailFragment, TAG_STEP_DETAIL_FRAGMENT)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_detail_recipe, mStepDetailFragment, TAG_STEP_DETAIL_FRAGMENT)
                        .commit();
            }
        }
    }

    @Override
    public boolean getTwoPane() {
        return mTwoPane;
    }
}
