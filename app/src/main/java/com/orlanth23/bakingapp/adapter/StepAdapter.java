package com.orlanth23.bakingapp.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.activity.RecipeDetailActivity;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.domain.Step;
import com.orlanth23.bakingapp.fragment.StepDetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by orlanth23 on 25/06/2017.
 */

public class StepAdapter
        extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private static final String BACKSTACK = "StepAdapterBackStack";

    private final Recipe mRecipe;
    private boolean mTwoPane;
    private AppCompatActivity mAppCompatActivity;

    public StepAdapter(@NonNull AppCompatActivity activity, Recipe recipe, boolean twoPane) {
        mRecipe = recipe;
        mTwoPane = twoPane;
        mAppCompatActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int stepIndex = position;
        holder.mStep = mRecipe.getSteps().get(stepIndex);
        holder.mShortDescriptionView.setText(holder.mStep.getShortDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle arguments = new Bundle();
                arguments.putInt(StepDetailFragment.ARG_STEP_INDEX, stepIndex);
                arguments.putParcelable(StepDetailFragment.ARG_RECIPE, mRecipe);

                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);

                if (mTwoPane) {
                    mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_step_container, fragment, RecipeDetailActivity.TAG_STEP_DETAIL_FRAGMENT)
                            .commit();
                } else {
                    mAppCompatActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_detail_recipe, fragment, RecipeDetailActivity.TAG_STEP_DETAIL_FRAGMENT)
                            .addToBackStack(BACKSTACK)
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipe.getSteps().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        Step mStep;

        @BindView(R.id.step_short_description)
        TextView mShortDescriptionView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }
    }
}
