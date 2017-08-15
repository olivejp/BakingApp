package com.orlanth23.bakingapp;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by orlanth23 on 15/08/2017.
 */

public class CustomActivityTestRule<A extends AppCompatActivity> extends ActivityTestRule<A> {

    public interface onBeforeListener{
        void onBefore();
    }

    public interface getIntentListener{
        Intent getIntent();
    }

    private onBeforeListener onBeforeListener;

    private getIntentListener getIntentListener;

    public CustomActivityTestRule(Class<A> activityClass) {
        super(activityClass);
        this.onBeforeListener = null;
        this.getIntentListener = null;
    }

    public CustomActivityTestRule(Class<A> activityClass, onBeforeListener onBeforeListener, getIntentListener getIntentListener) {
        super(activityClass);
        this.onBeforeListener = onBeforeListener;
        this.getIntentListener = getIntentListener;
    }



    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        onBeforeListener.onBefore();
    }

    @Override
    protected Intent getActivityIntent() {
        return getIntentListener.getIntent();
    }
}
