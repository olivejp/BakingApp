package com.orlanth23.bakingapp;

import android.content.Intent;
import android.support.annotation.Nullable;
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

    public CustomActivityTestRule(Class<A> activityClass, @Nullable onBeforeListener onBeforeListener, @Nullable getIntentListener getIntentListener) {
        super(activityClass);
        this.onBeforeListener = onBeforeListener;
        this.getIntentListener = getIntentListener;
    }



    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        if (onBeforeListener != null) {
            onBeforeListener.onBefore();
        }
    }

    @Override
    protected Intent getActivityIntent() {
        if (getIntentListener != null) {
            return getIntentListener.getIntent();
        }
        return null;
    }

    public CustomActivityTestRule.onBeforeListener getOnBeforeListener() {
        return onBeforeListener;
    }

    public void setOnBeforeListener(CustomActivityTestRule.onBeforeListener onBeforeListener) {
        this.onBeforeListener = onBeforeListener;
    }

    public CustomActivityTestRule.getIntentListener getGetIntentListener() {
        return getIntentListener;
    }

    public void setGetIntentListener(CustomActivityTestRule.getIntentListener getIntentListener) {
        this.getIntentListener = getIntentListener;
    }
}
