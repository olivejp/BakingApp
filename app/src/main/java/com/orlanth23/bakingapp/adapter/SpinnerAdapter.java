package com.orlanth23.bakingapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.domain.Recipe;

import java.util.ArrayList;

/**
 * Created by orlanth23 on 05/08/2017.
 */

public class SpinnerAdapter extends BaseAdapter {

    private Context context;
    private int res;
    private ArrayList<Recipe> navRecipeItems;

    public SpinnerAdapter(Context context, @LayoutRes int resource, ArrayList<Recipe> navRecipeItems) {
        super();

        this.context = context;
        this.res = resource;
        this.navRecipeItems = navRecipeItems;
    }

    @Override
    public int getCount() {
        return navRecipeItems.size();
    }

    @Override
    public Recipe getItem(int position) {
        return navRecipeItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return navRecipeItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(res, null);
        }

        TextView recipeName = convertView.findViewById(R.id.recipe_name);
        TextView recipeId = convertView.findViewById(R.id.recipe_id);

        recipeId.setText(String.valueOf(navRecipeItems.get(position).getId()));
        recipeName.setText(navRecipeItems.get(position).getName());

        return convertView;
    }

}
