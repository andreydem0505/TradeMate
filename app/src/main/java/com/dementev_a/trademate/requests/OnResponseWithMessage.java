package com.dementev_a.trademate.requests;

import android.os.Bundle;

import com.dementev_a.trademate.api.API;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;

import java.io.IOException;

import okhttp3.Response;

public abstract class OnResponseWithMessage {
    private final Response response;
    private final Bundle bundle;
    public String stringResponse;

    public OnResponseWithMessage(Response response, Bundle bundle) {
        this.response = response;
        this.bundle = bundle;
    }

    public void execute() {
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
    }

    public abstract void successMessage();
}
