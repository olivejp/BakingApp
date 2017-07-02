package com.orlanth23.bakingapp.fragment;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.adapter.IngredientAdapter;
import com.orlanth23.bakingapp.adapter.StepAdapter;
import com.orlanth23.bakingapp.domain.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment {

    private Recipe mRecipe;
    private boolean mTwoPane;

    @BindView(R.id.recipe_detail_list_ingredients)
    RecyclerView mRecyclerViewIngredients;
    @BindView(R.id.recipe_detail_list_steps)
    RecyclerView mRecyclerViewSteps;

    public RecipeDetailFragment() {
    }

    public static RecipeDetailFragment newInstance(Recipe recipe, boolean twoPane){
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setmRecipe(recipe);
        recipeDetailFragment.setmTwoPane(twoPane);
        return recipeDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        CollapsingToolbarLayout appBarLayout =  rootView.findViewById(R.id.toolbar_layout);
        appBarLayout.setTitle(mRecipe.getName());

        mRecyclerViewIngredients.setAdapter(new IngredientAdapter(mRecipe.getIngredients()));
        mRecyclerViewSteps.setAdapter(new StepAdapter((AppCompatActivity) this.getActivity(), mRecipe, mTwoPane));

        return rootView;
    }

    public void setmRecipe(Recipe mRecipe) {
        this.mRecipe = mRecipe;
    }

    public void setmTwoPane(boolean mTwoPane) {
        this.mTwoPane = mTwoPane;
    }
}
