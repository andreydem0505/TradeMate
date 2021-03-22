package com.dementev_a.trademate.requests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class AsyncRequest {
    private final Bundle bundle;

    public AsyncRequest() {
        bundle = new Bundle();
    }

    public void execute() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            sendRequest();
            handler.post(this::UIWork);
        });
    }

    public abstract void sendRequest();

    public abstract void UIWork();

    public Bundle getBundle() {
        return bundle;
    }
}
