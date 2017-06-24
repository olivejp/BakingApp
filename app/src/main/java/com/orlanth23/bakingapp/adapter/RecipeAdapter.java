package com.orlanth23.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.recipeDetailActivity;
import com.orlanth23.bakingapp.recipeDetailFragment;

import java.util.List;

/**
 * Created by orlanth23 on 24/06/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final List<Recipe> mValues;

    private boolean mTwoPane;

    private AppCompatActivity mContext;

    public RecipeAdapter(AppCompatActivity context, List<Recipe> recipes, boolean twoPane) {
        mContext = context;
        mValues = recipes;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mRecipe = mValues.get(position);
        holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
        holder.mContentView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If two panel, we call a fragment into recipe_detail_container
                // Otherwise we just call a new activity wich contain the same fragment.
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(recipeDetailFragment.ARG_RECIPE, holder.mRecipe);
                    recipeDetailFragment fragment = new recipeDetailFragment();
                    fragment.setArguments(arguments);
                    mContext.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, recipeDetailActivity.class);
                    intent.putExtra(recipeDetailFragment.ARG_RECIPE, holder.mRecipe);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Recipe mRecipe;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView =  view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
