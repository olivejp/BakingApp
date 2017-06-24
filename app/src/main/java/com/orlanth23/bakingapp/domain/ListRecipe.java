package com.orlanth23.bakingapp.domain;

import java.util.ArrayList;

/**
 * Created by orlanth23 on 24/06/2017.
 */

public class ListRecipe {
    private static final ListRecipe ourInstance = new ListRecipe();

    private ArrayList<Recipe> listRecipe;
    private boolean hasBeenLoaded;

    public static ListRecipe getInstance() {
        return ourInstance;
    }

    private ListRecipe() {
        listRecipe = new ArrayList<>();
        hasBeenLoaded = false;
    }

    public ArrayList<Recipe> getListRecipe() {
        return listRecipe;
    }

    public void setListRecipe(ArrayList<Recipe> listRecipe) {
        this.listRecipe = listRecipe;
    }

    public boolean hasBeenLoaded() {
        return hasBeenLoaded;
    }

    public void setHasBeenLoaded(boolean hasBeenLoaded) {
        this.hasBeenLoaded = hasBeenLoaded;
    }
}
