package com.orlanth23.bakingapp.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by orlanth23 on 01/07/2017.
 */

@ContentProvider(authority = RecipesProvider.AUTHORITY, database = RecipesDatabase.class)
public class RecipesProvider {
    static final String AUTHORITY = "com.orlanth23.bakingapp.RecipesProvider";


    @TableEndpoint(table = RecipesDatabase.LIST_INGREDIENT)
    public static class ListIngredient {
        @ContentUri(
                path = "ingredient",
                type = "vnd.android.cursor.dir/list",
                defaultSort = IngredientInterface._ID + " ASC")
        public static final Uri LIST_INGREDIENT = Uri.parse("content://" + AUTHORITY + "/ingredient");

        @InexactContentUri(
                path = "ingredient/#",
                name = "INGREDIENT_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = IngredientInterface._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/ingredient/" + id);
        }

    }

    @TableEndpoint(table = RecipesDatabase.LIST_STEP)
    public static class ListStep {
        @ContentUri(
                path = "step",
                type = "vnd.android.cursor.dir/list",
                defaultSort = StepInterface._ID + " ASC")
        public static final Uri LIST_STEP = Uri.parse("content://" + AUTHORITY + "/step");

        @InexactContentUri(
                path = "step/#",
                name = "STEP_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = StepInterface._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/step/" + id);
        }
    }

    @TableEndpoint(table = RecipesDatabase.LIST_RECIPE)
    public static class ListRecipe {
        @ContentUri(
                path = "recipe",
                type = "vnd.android.cursor.dir/list",
                defaultSort = RecipeInterface._ID + " ASC")
        public static final Uri LIST_RECIPE = Uri.parse("content://" + AUTHORITY + "/recipe");

        @InexactContentUri(
                path = "recipe/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = RecipeInterface._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/recipe/" + id);
        }
    }
}
