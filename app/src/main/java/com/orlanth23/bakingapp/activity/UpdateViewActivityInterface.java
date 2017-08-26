package com.orlanth23.bakingapp.activity;

/**
 * Created by orlanth23 on 25/08/2017.
 */

public interface UpdateViewActivityInterface<T> {
    void onCompleteUpdateView(T result);
    void onFailureUpdateView(Exception e);
}
