package com.dummy.androidvolleyrequestdb.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionStatus {
    private static Context context;

    public ConnectionStatus(Context context) {
        ConnectionStatus.context = context;
    }

    public static final boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
