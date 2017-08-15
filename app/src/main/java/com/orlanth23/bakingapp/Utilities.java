package com.orlanth23.bakingapp;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by orlanth23 on 13/08/2017.
 */

public class Utilities {
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
