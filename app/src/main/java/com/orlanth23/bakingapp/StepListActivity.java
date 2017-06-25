package com.orlanth23.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.orlanth23.bakingapp.adapter.StepAdapter;
import com.orlanth23.bakingapp.domain.Step;

import java.util.ArrayList;

public class StepListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private ArrayList<Step> mListSteps;

    private static final String ARG_LIST_STEP = "LISTE_STEP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_LIST_STEP)) {
            mListSteps = savedInstanceState.getParcelableArrayList(ARG_LIST_STEP);
        }

        Bundle bundle = getIntent().getExtras();
        mListSteps = bundle.getParcelableArrayList(ARG_LIST_STEP);

        setContentView(R.layout.activity_step_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.step_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_LIST_STEP, mListSteps);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new StepAdapter(this, mListSteps, mTwoPane));
    }


}
