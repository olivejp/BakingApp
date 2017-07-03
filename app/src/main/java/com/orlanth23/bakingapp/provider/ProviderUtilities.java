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

                String[] arguments = new String[]{String.valueOf(recipe.getId())};

                String selectionStep = StepInterface.RECIPE_ID + " = ?";
                String sortOrder = StepInterface._ID;
                Cursor cursorStep = context.getContentResolver().query(RecipesProvider.ListStep.LIST_STEP, null, selectionStep, arguments, sortOrder);
                if (cursorStep != null) {
                    ArrayList<Step> stepList = new ArrayList<>();
                    while (cursorStep.moveToNext()) {
                        Step step = ProviderUtilities.getStepFromCursor(cursorStep);
                        stepList.add(step);
                    }
                    cursorStep.close();
                    recipe.setSteps(stepList);
                }

                String selectionIngredient = IngredientInterface.RECIPE_ID + " = ?";
                Cursor cursorIngredient = context.getContentResolver().query(RecipesProvider.ListIngredient.LIST_INGREDIENT, null, selectionIngredient, arguments, null);
                if (cursorIngredient != null) {
                    ArrayList<Ingredient> ingredientList = new ArrayList<>();
                    while (cursorIngredient.moveToNext()) {
                        Ingredient ingredient = ProviderUtilities.getIngredientFromCursor(cursorIngredient);
                        ingredientList.add(ingredient);
                    }
                    cursorIngredient.close();
                    recipe.setIngredients(ingredientList);
                }

                recipeList.add(recipe);
            }
            cursorRecipe.close();
        }
        return recipeList;
    }

    public static void populateContentProviderFromList(Context context, ArrayList<Recipe> recipeList) {
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

