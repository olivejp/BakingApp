package com.orlanth23.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.domain.Step;
import com.orlanth23.bakingapp.fragment.StepDetailFragment;

public class StepDetailActivity extends AppCompatActivity {

    public static final String ARG_STEP_INDEX = "ARG_STEP_INDEX";
    public static final String ARG_RECIPE = "ARG_RECIPE";

    private int mStepIndex;
    private Recipe mRecipe;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_previous:
                    if (mStepIndex > 0) {
                        mStepIndex = mStepIndex - 1;
                        changeStep(mRecipe.getSteps().get(mStepIndex));
                    }
                    return true;
                case R.id.navigation_next:
                    if (mStepIndex < mRecipe.getSteps().size() - 1) {
                        mStepIndex = mStepIndex + 1;
                        changeStep(mRecipe.getSteps().get(mStepIndex));
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mStepIndex = getIntent().getIntExtra(ARG_STEP_INDEX, 0);
            mRecipe = getIntent().getParcelableExtra(ARG_RECIPE);
        } else {
            mStepIndex = savedInstanceState.getInt(ARG_STEP_INDEX);
            mRecipe = savedInstanceState.getParcelable(ARG_RECIPE);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        changeStep(mRecipe.getSteps().get(mStepIndex));
    }

    private void changeStep(Step step) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(StepDetailFragment.ARG_STEP_INDEX, step);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_STEP_INDEX, mStepIndex);
        outState.putParcelable(ARG_RECIPE, mRecipe);
    }
}
