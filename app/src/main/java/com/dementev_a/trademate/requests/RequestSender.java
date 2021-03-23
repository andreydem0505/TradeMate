package com.dementev_a.trademate.requests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class RequestSender {
    private final OkHttpClient client;
    private final Request request;
    private final Bundle bundle;
    private final Handler handler;
    private String stringResponse;

    public RequestSender(OkHttpClient client, Request request, Bundle bundle) {
        this.client = client;
        this.request = request;
        this.bundle = bundle;
        handler = new Handler(Looper.getMainLooper());
    }

    private void duringRequest() {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                bundle.putInt(API.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    stringResponse = response.body().string();
                    String message = JsonEngine.getStringFromJson(stringResponse, "message");
                    if (message.equals(API.SUCCESS_RESPONSE)) {
                        successMessage();
                        bundle.putInt(API.STATUS_KEY_BUNDLE, RequestStatus.STATUS_OK);
                    } else if (RequestErrors.errors.containsKey(message)) {
                        BundleEngine.putError(bundle, RequestErrors.errors.get(message));
                    } else {
                        bundle.putInt(API.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
                    }
                } catch (IOException e) {
                    bundle.putInt(API.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
                }
                handler.post(() -> UIWork());
            }
        });
    }

    public abstract void successMessage();

    public void UIWork() {}

    public void execute() {
        duringRequest();
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
