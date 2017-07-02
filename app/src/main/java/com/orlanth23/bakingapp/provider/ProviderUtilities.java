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

    public static Recipe getRecipeFromCursor(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(cursor.getColumnIndex(RecipeInterface._ID)));
        recipe.setName(cursor.getString(cursor.getColumnIndex(RecipeInterface.name)));
        recipe.setServings(cursor.getInt(cursor.getColumnIndex(RecipeInterface.servings)));
        recipe.setImage(cursor.getString(cursor.getColumnIndex(RecipeInterface.image)));
        return recipe;
    }

    public static Ingredient getIngredientFromCursor(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(IngredientInterface.ingredient)));
        ingredient.setMeasure(cursor.getString(cursor.getColumnIndex(IngredientInterface.measure)));
        ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(IngredientInterface.quantity)));
        return ingredient;
    }

    public static Step getStepFromCursor(Cursor cursor) {
        Step step = new Step();
        step.setId(cursor.getInt(cursor.getColumnIndex(StepInterface._ID)));
        step.setDescription(cursor.getString(cursor.getColumnIndex(StepInterface.description)));
        step.setShortDescription(cursor.getString(cursor.getColumnIndex(StepInterface.shortDescription)));
        step.setThumbnailURL(cursor.getString(cursor.getColumnIndex(StepInterface.thumbnailURL)));
        step.setVideoURL(cursor.getString(cursor.getColumnIndex(StepInterface.videoURL)));
        return step;
    }

    public static ArrayList<Recipe> getListRecipeFromContentProvider(Context context) {
        ArrayList<Recipe> recipeList = null;
        Cursor cursorRecipe = context.getContentResolver().query(RecipesProvider.ListRecipe.LIST_RECIPE, null, null, null, null);
        if (cursorRecipe != null) {
            recipeList = new ArrayList<>();
            while (cursorRecipe.moveToNext()) {
                Recipe recipe = ProviderUtilities.getRecipeFromCursor(cursorRecipe);

                Cursor cursorStep = context.getContentResolver().query(RecipesProvider.ListStep.withRecipeId(recipe.getId()), null, null, null, null);
                if (cursorStep != null) {
                    while (cursorStep.moveToNext()) {
                        Step step = ProviderUtilities.getStepFromCursor(cursorStep);
                        recipe.getSteps().add(step);
                    }
                    cursorStep.close();
                }

                Cursor cursorIngredient = context.getContentResolver().query(RecipesProvider.ListIngredient.withRecipeId(recipe.getId()), null, null, null, null);
                if (cursorIngredient != null) {
                    while (cursorIngredient.moveToNext()) {
                        Ingredient ingredient = ProviderUtilities.getIngredientFromCursor(cursorIngredient);
                        recipe.getIngredients().add(ingredient);
                    }
                    cursorIngredient.close();
                }

                recipeList.add(recipe);
            }
            cursorRecipe.close();
        }
        return recipeList;
    }

    public static void populateContentProviderFromJson(Context context, ArrayList<Recipe> recipeList) {
        context.getContentResolver().delete(RecipesProvider.ListIngredient.LIST_INGREDIENT, null, null);
        context.getContentResolver().delete(RecipesProvider.ListStep.LIST_STEP, null, null);
        context.getContentResolver().delete(RecipesProvider.ListRecipe.LIST_RECIPE, null, null);
        for (Recipe recipe :
                recipeList) {
            for (Step step :
                    recipe.getSteps()) {
                ContentValues cvStep = new ContentValues();
                cvStep.put(StepInterface.RECIPE_ID, recipe.getId());
                cvStep.put(StepInterface.thumbnailURL, step.getThumbnailURL());
                cvStep.put(StepInterface.videoURL, step.getVideoURL());
                cvStep.put(StepInterface.shortDescription, step.getShortDescription());
                cvStep.put(StepInterface.description, step.getDescription());
                context.getContentResolver().insert(RecipesProvider.ListStep.LIST_STEP, cvStep);
            }

            for (Ingredient ingredient :
                    recipe.getIngredients()) {
                ContentValues cvIngredient = new ContentValues();
                cvIngredient.put(IngredientInterface.RECIPE_ID, recipe.getId());
                cvIngredient.put(IngredientInterface.quantity, ingredient.getQuantity());
                cvIngredient.put(IngredientInterface.measure, ingredient.getQuantity());
                cvIngredient.put(IngredientInterface.ingredient, ingredient.getQuantity());
                context.getContentResolver().insert(RecipesProvider.ListIngredient.LIST_INGREDIENT, cvIngredient);
            }

            ContentValues cvRecipe = new ContentValues();
            cvRecipe.put(RecipeInterface._ID, recipe.getId());
            cvRecipe.put(RecipeInterface.name, recipe.getName());
            cvRecipe.put(RecipeInterface.servings, recipe.getServings());
            cvRecipe.put(RecipeInterface.image, recipe.getImage());
            context.getContentResolver().insert(RecipesProvider.ListRecipe.LIST_RECIPE, cvRecipe);
        }
    }
}

