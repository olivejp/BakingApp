package com.orlanth23.bakingapp.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by orlanth23 on 01/07/2017.
 */

public interface RecipeInterface {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull String name = "name";
    @DataType(INTEGER) @NotNull String servings = "servings";
    @DataType(TEXT) @NotNull String image = "image";
}
