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
import com.orlanth23.bakingapp.RecipeDetailActivity;
import com.orlanth23.bakingapp.StepDetailActivity;
import com.orlanth23.bakingapp.StepDetailFragment;
import com.orlanth23.bakingapp.domain.Step;

import java.util.ArrayList;

/**
 * Created by orlanth23 on 25/06/2017.
 */

public class StepAdapter
        extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private final ArrayList<Step> mListSteps;
    private boolean mTwoPane;
    private AppCompatActivity mContext;

    public StepAdapter(AppCompatActivity context, ArrayList<Step> items, boolean twoPane) {
        mListSteps = items;
        mTwoPane = twoPane;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mStep = mListSteps.get(position);
        holder.mIdView.setText(mListSteps.get(position).getId());
        holder.mContentView.setText(mListSteps.get(position).getShortDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(StepDetailFragment.ARG_STEP, holder.mStep);
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    ((RecipeDetailActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    intent.putExtra(StepDetailFragment.ARG_STEP, holder.mStep);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Step mStep;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
