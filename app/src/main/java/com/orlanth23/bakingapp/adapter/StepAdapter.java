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
import com.orlanth23.bakingapp.activity.StepDetailActivity;
import com.orlanth23.bakingapp.domain.Step;
import com.orlanth23.bakingapp.fragment.StepDetailFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by orlanth23 on 25/06/2017.
 */

public class StepAdapter
        extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private final ArrayList<Step> mListSteps;
    private boolean mTwoPane;
    private AppCompatActivity mAppCompatActivity;

    public StepAdapter(AppCompatActivity activity, ArrayList<Step> items, boolean twoPane) {
        mListSteps = items;
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
        holder.mStep = mListSteps.get(position);
        holder.mShortDescriptionView.setText(mListSteps.get(position).getShortDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(StepDetailFragment.ARG_STEP, holder.mStep);
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_step_container, fragment)
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
