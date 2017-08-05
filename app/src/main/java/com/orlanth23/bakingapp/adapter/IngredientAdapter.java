package com.orlanth23.bakingapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.domain.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by orlanth23 on 25/06/2017.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private final ArrayList<Ingredient> mIngredients;

    public IngredientAdapter(ArrayList<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_content, parent, false);
        return new IngredientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IngredientAdapter.ViewHolder holder, int position) {
        holder.mIngredient = mIngredients.get(position);
        holder.mIngredientNameView.setText(String.valueOf(mIngredients.get(position).getIngredient()));
        holder.mIngredientMeasureQuantityView.setText(mIngredients.get(position).getMeasureQuantity());
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        Ingredient mIngredient;
        @BindView(R.id.ingredient_name)
        TextView mIngredientNameView;
        @BindView(R.id.ingredient_measure_quantity)
        TextView mIngredientMeasureQuantityView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }
    }
}
