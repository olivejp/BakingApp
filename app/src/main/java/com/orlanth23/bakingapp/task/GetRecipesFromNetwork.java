package com.orlanth23.bakingapp.task;

import android.os.AsyncTask;

import com.orlanth23.bakingapp.Constants;
import com.orlanth23.bakingapp.network.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by orlanth23 on 25/08/2017.
 */

public class GetRecipesFromNetwork extends AsyncTask<Void, Void, String> {

    private AsyncTaskCompleteListener<String> listener;

    /**
     * UnComment the different lines
     * and replace the GetRecipesFromNetwork signature to test my own Json file
     **/
    // private Context mContext;

    // public GetRecipesFromNetwork(Context context, AsyncTaskCompleteListener<String> asyncTaskCompleteListener) {
    public GetRecipesFromNetwork(AsyncTaskCompleteListener<String> asyncTaskCompleteListener) {
        //this.mContext = context;
        this.listener = asyncTaskCompleteListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return NetworkUtils.makeServiceCall(new URL(Constants.API_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        // json = Utilities.loadJSONFromAsset(mContext, "recipe_list.json");
        listener.onTaskComplete(json);
    }
}