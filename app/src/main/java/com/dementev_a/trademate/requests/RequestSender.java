package com.dementev_a.trademate.requests;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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

public class RequestSender {
    private Context context;
    private OkHttpClient client;
    private Request request;
    private Bundle bundle;
    private Handler handler;
    private int handleNumber;
    private String stringResponse;

    public RequestSender() {}

    public RequestSender(Context context, OkHttpClient client, Request request, Handler handler, int handleNumber) {
        this.context = context;
        this.client = client;
        this.request = request;
        this.bundle = new Bundle();
        this.handler = handler;
        this.handleNumber = handleNumber;
    }

    private void duringRequest() {
        if (!RequestEngine.isConnectedToInternet(context)) {
            getBundle().putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_INTERNET_ERROR);
            sendHandlerMessage(bundle, handler, handleNumber);
            return;
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    stringResponse = response.body().string();
                    switch (response.code()) {
                        case 200: {
                            String message = JsonEngine.getStringFromJson(stringResponse, "message");
                            if (message.equals(API.SUCCESS_RESPONSE)) {
                                successMessage();
                                bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_OK);
                            } else if (RequestErrors.errors.containsKey(message)) {
                                BundleEngine.putError(bundle, RequestErrors.errors.get(message));
                            } else {
                                bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
                            }
                        } break;
                        case 400: {
                            String message = JsonEngine.getInputError(stringResponse, "violations");
                            if (RequestErrors.errors.containsKey(message)) {
                                BundleEngine.putError(bundle, RequestErrors.errors.get(message));
                            } else {
                                bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
                            }
                        } break;
                        default: {
                            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
                        } break;
                    }
                } catch (IOException e) {
                    bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
                }
                sendHandlerMessage(bundle, handler, handleNumber);
            }
        });
    }

    public void successMessage() {}

    public void sendHandlerMessage(Bundle bundle, Handler handler, int handleNumber) {
        Message message = Message.obtain();
        message.what = handleNumber;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public void execute() {
        duringRequest();
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
