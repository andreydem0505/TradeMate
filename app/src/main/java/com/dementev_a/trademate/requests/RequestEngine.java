package com.dementev_a.trademate.requests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class RequestEngine {
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
