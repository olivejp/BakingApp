package com.orlanth23.bakingapp.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by orlanth23 on 01/07/2017.
 */

@Database(version = RecipesDatabase.VERSION)
class RecipesDatabase {
    @Table(RecipeInterface.class)
    static final String LIST_RECIPE = "listRecipe";
    @Table(IngredientInterface.class)
    static final String LIST_INGREDIENT = "listIngredient";
    @Table(StepInterface.class)
    static final String LIST_STEP = "listStep";
    static final int VERSION = 1;
}
