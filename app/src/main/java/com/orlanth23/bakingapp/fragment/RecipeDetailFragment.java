package com.orlanth23.bakingapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.adapter.IngredientAdapter;
import com.orlanth23.bakingapp.adapter.StepAdapter;
import com.orlanth23.bakingapp.domain.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment {

    private static final String TAG = RecipeDetailFragment.class.getName();

    public static final String ARG_RECIPE = "recipe";

    @BindView(R.id.recipe_detail_list_ingredients)
    RecyclerView mRecyclerViewIngredients;
    @BindView(R.id.recipe_detail_list_steps)
    RecyclerView mRecyclerViewSteps;
    @BindView(R.id.recipe_detail_image)
    ImageView mImageRecipe;
    private Recipe mRecipe;
    private AppCompatActivity mActivity;
    private GetTwoPaneActivity mTwoPaneActivity;

    public RecipeDetailFragment() {
    }

    public static RecipeDetailFragment newInstance(Recipe recipe) {
        Bundle bundle = new Bundle();
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        bundle.putParcelable(ARG_RECIPE, recipe);
        recipeDetailFragment.setArguments(bundle);
        return recipeDetailFragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(ARG_RECIPE)) {
                mRecipe = bundle.getParcelable(ARG_RECIPE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivity = (AppCompatActivity) context;
        } catch (ClassCastException e) {
            Log.e(TAG, getString(R.string.recipedetailfragment_need_appcompatactivity), e);
        }

        try {
            mTwoPaneActivity = (GetTwoPaneActivity) context;
        } catch (ClassCastException e) {
            Log.e(TAG, getString(R.string.recipedetailfragment_need_gettwopaneactivity), e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        readBundle(getArguments());

        ButterKnife.bind(this, rootView);

        mRecyclerViewIngredients.setAdapter(new IngredientAdapter(mRecipe.getIngredients()));
        mRecyclerViewSteps.setAdapter(new StepAdapter(mActivity, mRecipe, mTwoPaneActivity.getTwoPane()));

        if (!TextUtils.isEmpty(mRecipe.getImage())) {
            mImageRecipe.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load(mRecipe.getImage())
                    .centerCrop()
                    .into(mImageRecipe);
        } else {
            mImageRecipe.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void setRecipe(Recipe mRecipe) {
        this.mRecipe = mRecipe;
    }

    public interface GetTwoPaneActivity {
        boolean getTwoPane();
    }
}
