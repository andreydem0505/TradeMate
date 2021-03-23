package com.dementev_a.trademate.requests;

import android.os.Bundle;

public abstract class DataReceiver {
    private final Bundle bundle;

    public DataReceiver() {
        bundle = new Bundle();
    }

    public void execute() {
        sendRequests();
        UIWork();
    }

    public abstract void sendRequests();

    public abstract void UIWork();

    public Bundle getBundle() {
        return bundle;
    }
}
