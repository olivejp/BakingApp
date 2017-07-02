package com.orlanth23.bakingapp.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by orlanth23 on 01/07/2017.
 */

public interface IngredientInterface {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement String _ID = "_id";
    @DataType(REAL) @NotNull String quantity = "quantity";
    @DataType(TEXT) @NotNull String measure = "measure";
    @DataType(TEXT) @NotNull String ingredient = "ingredient";
    @DataType(INTEGER) @NotNull String RECIPE_ID = "recipe_id";

}
