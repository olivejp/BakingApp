package com.orlanth23.bakingapp;

import android.text.TextUtils;

import com.orlanth23.bakingapp.network.NetworkUtils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NetworkUtilsTest {

    private String getJsonFromInternet() {
        return NetworkUtils.makeServiceCall(MainActivity.API_URL);
    }

    @Test
    public void NetworkUtils_makeServiceCalltest() {
        // When
        String jsonResponse = getJsonFromInternet();

        // Then
        assertTrue(jsonResponse != null && !TextUtils.isEmpty(jsonResponse));
        Assert.assertNotEquals("", jsonResponse);
    }
}