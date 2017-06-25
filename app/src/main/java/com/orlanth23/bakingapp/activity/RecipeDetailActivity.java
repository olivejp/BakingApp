package com.orlanth23.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.fragment.RecipeDetailFragment;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Are we in two panel mode
        boolean mTwoPane = false;
        if (findViewById(R.id.frame_step_container) != null) {
            mTwoPane = true;
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Recipe mRecipe = getIntent().getParcelableExtra(RecipeDetailFragment.ARG_RECIPE);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeDetailFragment.ARG_RECIPE, mRecipe);
            arguments.putBoolean(RecipeDetailFragment.ARG_TWO_PANE, mTwoPane);
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_detail_recipe, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
