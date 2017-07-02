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

public interface StepInterface {
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement String _ID = "_id";
    @DataType(TEXT) @NotNull String shortDescription = "shortDescription";
    @DataType(TEXT) @NotNull String description = "description";
    @DataType(TEXT) String videoURL = "videoURL";
    @DataType(TEXT) String thumbnailURL = "thumbnailURL";
    @DataType(INTEGER) @NotNull String RECIPE_ID = "recipe_id";
}
