package com.orlanth23.bakingapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by orlanth23 on 13/07/2017.
 */

public class NetworkReceiver extends BroadcastReceiver {

    public static final IntentFilter CONNECTIVITY_CHANGE_INTENT_FILTER = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private NetworkChangeListener mNetworkChangeListener;

    public NetworkReceiver(NetworkChangeListener networkChangeListener) {
        super();
        mNetworkChangeListener = networkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkConnection(context);
    }

    public boolean checkConnection(Context context) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            mNetworkChangeListener.OnNetworkEnable();
            return true;
        } else if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            mNetworkChangeListener.OnNetworkDisable();
            return false;
        }
        return false;
    }

    public interface NetworkChangeListener {
        void OnNetworkEnable();

        void OnNetworkDisable();
    }
}
