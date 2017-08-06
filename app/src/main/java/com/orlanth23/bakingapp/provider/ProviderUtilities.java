package com.orlanth23.bakingapp.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.orlanth23.bakingapp.domain.Ingredient;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.domain.Step;

import java.util.ArrayList;

/**
 * Created by orlanth23 on 02/07/2017.
 */

public class ProviderUtilities {

    private static Recipe getRecipeFromCursor(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(cursor.getColumnIndex(RecipeInterface._ID)));
        recipe.setName(cursor.getString(cursor.getColumnIndex(RecipeInterface.name)));
        recipe.setServings(cursor.getInt(cursor.getColumnIndex(RecipeInterface.servings)));
        recipe.setImage(cursor.getString(cursor.getColumnIndex(RecipeInterface.image)));
        return recipe;
    }

    private static Ingredient getIngredientFromCursor(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(IngredientInterface.ingredient)));
        ingredient.setMeasure(cursor.getString(cursor.getColumnIndex(IngredientInterface.measure)));
        ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(IngredientInterface.quantity)));
        return ingredient;
    }

    private static Step getStepFromCursor(Cursor cursor) {
        Step step = new Step();
        step.setId(cursor.getInt(cursor.getColumnIndex(StepInterface._ID)));
        step.setDescription(cursor.getString(cursor.getColumnIndex(StepInterface.description)));
        step.setShortDescription(cursor.getString(cursor.getColumnIndex(StepInterface.shortDescription)));
        step.setThumbnailURL(cursor.getString(cursor.getColumnIndex(StepInterface.thumbnailURL)));
        step.setVideoURL(cursor.getString(cursor.getColumnIndex(StepInterface.videoURL)));
        return step;
    }

    /**
     * Qurey the content provider with the recipe id to get a list of step
     *
     * @param context
     * @param recipeId
     * @return Recipe
     */
    public static Recipe getRecipeByRecipeId(Context context, long recipeId) {
        ArrayList<Recipe> listRecipe = getListRecipeFromContentProvider(context);
        for (Recipe recipe : listRecipe) {
            if (recipe.getId() == recipeId) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Qurey the content provider with the recipe id to get a list of step
     *
     * @param context
     * @param recipeId
     * @return ArrayList<Step>
     */
    private static ArrayList<Step> getStepListByRecipeId(Context context, long recipeId) {
        ArrayList<Step> stepList = new ArrayList<>();
        String[] arguments = new String[]{String.valueOf(recipeId)};
        String selectionStep = StepInterface.RECIPE_ID + " = ?";
        String sortOrder = StepInterface._ID;
        Cursor cursorStep = context.getContentResolver().query(RecipesProvider.ListStep.LIST_STEP, null, selectionStep, arguments, sortOrder);
        if (cursorStep != null) {
            while (cursorStep.moveToNext()) {
                Step step = ProviderUtilities.getStepFromCursor(cursorStep);
                stepList.add(step);
            }
            cursorStep.close();
        }
        return stepList;
    }

    /**
     * Qurey the content provider with the recipe id to get a list of ingredient
     *
     * @param context
     * @param recipeId
     * @return ArrayList<Ingredient>
     */
    public static ArrayList<Ingredient> getIngredientListByRecipeId(Context context, long recipeId) {
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        String[] arguments = new String[]{String.valueOf(recipeId)};
        String selectionIngredient = IngredientInterface.RECIPE_ID + " = ?";
        Cursor cursorIngredient = context.getContentResolver().query(RecipesProvider.ListIngredient.LIST_INGREDIENT, null, selectionIngredient, arguments, null);
        if (cursorIngredient != null) {
            while (cursorIngredient.moveToNext()) {
                Ingredient ingredient = ProviderUtilities.getIngredientFromCursor(cursorIngredient);
                ingredientList.add(ingredient);
            }
            cursorIngredient.close();
        }
        return ingredientList;
    }

    /**
     * Query the ContentProvider from the context to get the recipe list
     * @param context
     * @return List of recipe
     */
    public static ArrayList<Recipe> getListRecipeFromContentProvider(Context context) {
        ArrayList<Recipe> recipeList = null;

        // Query the content provider to get a cursor of recipe
        Cursor cursorRecipe = context.getContentResolver().query(RecipesProvider.ListRecipe.LIST_RECIPE, null, null, null, null);

        if (cursorRecipe != null) {
            recipeList = new ArrayList<>();
            while (cursorRecipe.moveToNext()) {
                Recipe recipe = ProviderUtilities.getRecipeFromCursor(cursorRecipe);
                recipe.setSteps(getStepListByRecipeId(context, recipe.getId()));
                recipe.setIngredients(getIngredientListByRecipeId(context, recipe.getId()));
                recipeList.add(recipe);
            }
            cursorRecipe.close();
        }
        return recipeList;
    }

    /**
     * Populate the content provider with the recipe list
     *
     * @param context
     * @param recipeList
     */
    public static void populateContentProviderFromList(Context context, ArrayList<Recipe> recipeList) {

        // Delete all the content
        context.getContentResolver().delete(RecipesProvider.ListIngredient.LIST_INGREDIENT, null, null);
        context.getContentResolver().delete(RecipesProvider.ListStep.LIST_STEP, null, null);
        context.getContentResolver().delete(RecipesProvider.ListRecipe.LIST_RECIPE, null, null);

        ContentValues contentValues = new ContentValues();
        for (Recipe recipe :
                recipeList) {
            for (Step step :
                    recipe.getSteps()) {
                contentValues.clear();
                contentValues.put(StepInterface.RECIPE_ID, recipe.getId());
                contentValues.put(StepInterface.thumbnailURL, step.getThumbnailURL());
                contentValues.put(StepInterface.videoURL, step.getVideoURL());
                contentValues.put(StepInterface.shortDescription, step.getShortDescription());
                contentValues.put(StepInterface.description, step.getDescription());
                context.getContentResolver().insert(RecipesProvider.ListStep.LIST_STEP, contentValues);
            }

            for (Ingredient ingredient :
                    recipe.getIngredients()) {
                contentValues.clear();
                contentValues.put(IngredientInterface.RECIPE_ID, recipe.getId());
                contentValues.put(IngredientInterface.quantity, ingredient.getQuantity());
                contentValues.put(IngredientInterface.measure, ingredient.getMeasure());
                contentValues.put(IngredientInterface.ingredient, ingredient.getIngredient());
                context.getContentResolver().insert(RecipesProvider.ListIngredient.LIST_INGREDIENT, contentValues);
            }

            contentValues.clear();
            contentValues.put(RecipeInterface._ID, recipe.getId());
            contentValues.put(RecipeInterface.name, recipe.getName());
            contentValues.put(RecipeInterface.servings, recipe.getServings());
            contentValues.put(RecipeInterface.image, recipe.getImage());
            context.getContentResolver().insert(RecipesProvider.ListRecipe.LIST_RECIPE, contentValues);
        }
    }
}

