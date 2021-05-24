package com.dementev_a.trademate.bundle;

import android.os.Bundle;

import com.dementev_a.trademate.requests.RequestStatus;

import org.jetbrains.annotations.NotNull;

public class BundleEngine {
    public static final String
            STATUS_KEY_BUNDLE = "status",
            NAME_KEY_BUNDLE = "name",
            ID_KEY_BUNDLE = "id",
            ERROR_TEXT_KEY_BUNDLE = "error_text",
            TOTAL_OPERATORS_KEY_BUNDLE = "total_operators",
            TOTAL_MERCHANDISERS_KEY_BUNDLE = "total_merchandisers",
            TOTAL_REQUESTS_KEY_BUNDLE = "total_requests",
            TOTAL_SHOPS_KEY_BUNDLE = "total_shops",
            TOTAL_PHOTO_REPORTS_KEY_BUNDLE = "total_photo_reports",
            TOTAL_PHOTOS_KEY_BUNDLE = "total_photos",
            MERCHANDISERS_KEY_BUNDLE = "merchandisers",
            REQUESTS_KEY_BUNDLE = "requests",
            SHOPS_KEY_BUNDLE = "shops",
            PHOTO_REPORTS_KEY_BUNDLE = "photo_reports",
            NAMES_OF_OPERATORS_KEY_BUNDLE = "namesOfOperators",
            EMAILS_OF_OPERATORS_KEY_BUNDLE = "emailsOfOperators",
            COMPANY_NAME_KEY_BUNDLE = "companyName",
            MERCHANDISER_NAME_KEY_BUNDLE = "merchandiserName",
            ACCESS_TOKEN_KEY_BUNDLE = "accessToken",
            SHOP_NAME_KEY_BUNDLE = "shopName",
            PHOTO_REPORT_NAME_KEY_BUNDLE = "photoReportName";

    public static void putError(@NotNull Bundle bundle, int id) {
        bundle.putInt("status", RequestStatus.STATUS_ERROR_TEXT);
        bundle.putInt(ERROR_TEXT_KEY_BUNDLE, id);
    }
}
