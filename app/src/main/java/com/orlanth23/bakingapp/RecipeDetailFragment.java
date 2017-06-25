package com.orlanth23.bakingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orlanth23.bakingapp.adapter.IngredientAdapter;
import com.orlanth23.bakingapp.adapter.StepAdapter;
import com.orlanth23.bakingapp.domain.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment {

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_TWO_PANE = "twoPane";

    private Recipe mRecipe;
    private boolean mTwoPane;

    @BindView(R.id.recipe_detail)
    TextView mRecipeName;
    @BindView(R.id.recipe_detail_list_ingredients)
    RecyclerView mRecyclerViewIngredients;
    @BindView(R.id.recipe_detail_list_steps)
    RecyclerView mRecyclerViewSteps;

    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECIPE)) {
            mRecipe = getArguments().getParcelable(ARG_RECIPE);
        }
        if (getArguments().containsKey(ARG_TWO_PANE)) {
            mTwoPane = getArguments().getBoolean(ARG_TWO_PANE);
        }

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mRecipe.getName());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(rootView);

        if (mRecipe != null) {
            mRecipeName.setText(mRecipe.getName());
            mRecyclerViewIngredients.setAdapter(new IngredientAdapter(mRecipe.getIngredients()));
            mRecyclerViewSteps.setAdapter(new StepAdapter((AppCompatActivity) this.getActivity(), mRecipe.getSteps(), mTwoPane));
        }

        return rootView;
    }
}
