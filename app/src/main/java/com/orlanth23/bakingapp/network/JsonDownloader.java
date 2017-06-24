package com.orlanth23.bakingapp.network;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.orlanth23.bakingapp.IdlingResource.SimpleIdlingResource;
import com.orlanth23.bakingapp.domain.Recipe;

import java.util.ArrayList;

/**
 * Created by orlanth23 on 25/06/2017.
 */

public class JsonDownloader {

    private static final int DELAY_MILLIS = 3000;

    final static ArrayList<Recipe> mList = new ArrayList<>();

    interface DelayerCallback{
        void onDone(ArrayList<Recipe> listRecipes);
    }

    static void downloadJson(Context context, final DelayerCallback callback,
                              @Nullable final SimpleIdlingResource idlingResource) {

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        // Display a toast to let the user know the images are downloading
        Toast.makeText(context, "La liste des recettes est en cours de téléchargement", Toast.LENGTH_LONG).show();


        // Fill ArrayList with Tea objects
        mTeas.add(new Tea(context.getString(R.string.black_tea_name), R.drawable.black_tea));
        mTeas.add(new Tea(context.getString(R.string.green_tea_name), R.drawable.green_tea));
        mTeas.add(new Tea(context.getString(R.string.white_tea_name), R.drawable.white_tea));
        mTeas.add(new Tea(context.getString(R.string.oolong_tea_name), R.drawable.oolong_tea));
        mTeas.add(new Tea(context.getString(R.string.honey_lemon_tea_name), R.drawable.honey_lemon_tea));
        mTeas.add(new Tea(context.getString(R.string.chamomile_tea_name), R.drawable.chamomile_tea));


        /**
         * {@link postDelayed} allows the {@link Runnable} to be run after the specified amount of
         * time set in DELAY_MILLIS elapses. An object that implements the Runnable interface
         * creates a thread. When this thread starts, the object's run method is called.
         *
         * After the time elapses, if there is a callback we return the image resource ID and
         * set the idle state to true.
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onDone(mTeas);
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }
        }, DELAY_MILLIS);
    }
}
