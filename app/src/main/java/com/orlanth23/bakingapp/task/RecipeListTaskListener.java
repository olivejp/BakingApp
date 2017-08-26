package com.orlanth23.bakingapp.task;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.orlanth23.bakingapp.activity.UpdateViewActivityInterface;
import com.orlanth23.bakingapp.domain.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by orlanth23 on 25/08/2017.
 */

public class RecipeListTaskListener implements AsyncTaskCompleteListener<String> {

    private UpdateViewActivityInterface<ArrayList<Recipe>> updatedActivity;

    public RecipeListTaskListener(UpdateViewActivityInterface<ArrayList<Recipe>> updatedActivity) {
        this.updatedActivity = updatedActivity;
    }

    @Override
    public void onTaskComplete(final String json) {
        if (json != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Recipe>>() {
            }.getType();
            try {
                final ArrayList<Recipe> tempList = gson.fromJson(json, listType);
                updatedActivity.onCompleteUpdateView(tempList);
            } catch (JsonSyntaxException e) {
                updatedActivity.onFailureUpdateView(e);
            }
        }
    }
}
