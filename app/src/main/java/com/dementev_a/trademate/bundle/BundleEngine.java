package com.dementev_a.trademate.bundle;

import android.os.Bundle;

import com.dementev_a.trademate.requests.RequestStatus;

import org.jetbrains.annotations.NotNull;

public class BundleEngine {
    public static final String
            STATUS_KEY_BUNDLE = "status",
            ERROR_TEXT_KEY_BUNDLE = "error_text",
            TOTAL_OPERATORS_KEY_BUNDLE = "total_operators",
            TOTAL_MERCHANDISERS_KEY_BUNDLE = "total_merchandisers",
            TOTAL_REQUESTS_KEY_BUNDLE = "total_requests",
            TOTAL_SHOPS_KEY_BUNDLE = "total_shops",
            MERCHANDISERS_KEY_BUNDLE = "merchandisers",
            REQUESTS_KEY_BUNDLE = "requests",
            SHOPS_KEY_BUNDLE = "shops",
            NAMES_OF_OPERATORS_KEY_BUNDLE = "namesOfOperators",
            EMAILS_OF_OPERATORS_KEY_BUNDLE = "emailsOfOperators",
            COMPANY_NAME_KEY_BUNDLE = "companyName",
            ACCESS_TOKEN_KEY_BUNDLE = "accessToken";

    public static void putError(@NotNull Bundle bundle, int id) {
        bundle.putInt("status", RequestStatus.STATUS_ERROR_TEXT);
        bundle.putInt(ERROR_TEXT_KEY_BUNDLE, id);
    }
}
