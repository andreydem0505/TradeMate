package com.dementev_a.trademate.api;

import android.os.Bundle;

import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.requests.RequestEngine;
import com.dementev_a.trademate.requests.RequestStatus;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class API {
    public static final String
            MAIN_URL = "https://trademate-api.herokuapp.com",
            AUTH_COMPANY_URL = "/auth/company",
            SIGN_UP_COMPANY_URL = "/register/company",
            AUTH_MERCHANDISER_URL = "/auth/merchandiser",
            ADD_MERCHANDISER_URL = "/register/merchandiser",
            ALL_MERCHANDISERS_URL = "/merchandisers",
            ADD_OPERATOR_URL = "/register/operator",
            ALL_OPERATORS_URL = "/operators",
            CREATE_REQUEST_URL = "/create/request",
            GET_ALL_REQUESTS_URL = "/requests",
            GET_ALL_SHOPS_URL = "/shops",
            ADD_SHOP_URL = "/create/shop";


    // using without error text
    public static void getOperators(Bundle bundle, Map<String, String> headers) {
        String url = API.MAIN_URL + API.ALL_OPERATORS_URL;
        try {
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = JsonEngine.getStringFromJson(response, "message");
            if ("Success".equals(message)) {
                int total = JsonEngine.getIntegerFromJson(response, "total");
                bundle.putInt("total_operators", total);

                bundle.putAll(JsonEngine.getOperatorsArrayFromJson(response, "operators"));

                bundle.putInt("status", RequestStatus.STATUS_OK);
            } else {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }

    public static void getMerchandisers(Bundle bundle, Map<String, String> headers) {
        String url = API.MAIN_URL + API.ALL_MERCHANDISERS_URL;
        try {
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = JsonEngine.getStringFromJson(response, "message");
            if ("Success".equals(message)) {
                int total = JsonEngine.getIntegerFromJson(response, "total");
                bundle.putInt("total_merchandisers", total);

                MerchandiserJson[] merchandisersArray = JsonEngine.getMerchandisersArrayFromJson(response, "merchandisers");
                bundle.putParcelableArray("merchandisers", merchandisersArray);

                bundle.putInt("status", RequestStatus.STATUS_OK);
            } else {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }

    public static void getRequestsToday(Bundle bundle, Map<String, String> headers, @NotNull String... merchandiser) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Clock clock = Clock.systemUTC();
        LocalDate localDate = LocalDate.now(clock);
        String formattedDate = localDate.format(formatter);
        String url = API.MAIN_URL + API.GET_ALL_REQUESTS_URL + "?date=" + formattedDate;
        if (merchandiser.length > 0)
            url += "&name=" + merchandiser[0];
        try {
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = JsonEngine.getStringFromJson(response, "message");
            if ("Success".equals(message)) {
                int total = JsonEngine.getIntegerFromJson(response, "total");
                bundle.putInt("total_requests", total);

                RequestJson[] requestsArray = JsonEngine.getRequestsArrayFromJson(response, "requests");
                bundle.putParcelableArray("requests", requestsArray);

                bundle.putInt("status", RequestStatus.STATUS_OK);
            } else {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }

    public static void getRequestsForDate(Bundle bundle, Map<String, String> headers, @NotNull LocalDate localDate, @NotNull String... merchandiser) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDate.format(formatter);
        String url = API.MAIN_URL + API.GET_ALL_REQUESTS_URL + "?date=" + formattedDate;
        if (merchandiser.length > 0)
            url += "&name=" + merchandiser[0];
        try {
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = JsonEngine.getStringFromJson(response, "message");
            if ("Success".equals(message)) {
                int total = JsonEngine.getIntegerFromJson(response, "total");
                bundle.putInt("total_requests", total);

                RequestJson[] requestsArray = JsonEngine.getRequestsArrayFromJson(response, "requests");
                bundle.putParcelableArray("requests", requestsArray);

                bundle.putInt("status", RequestStatus.STATUS_OK);
            } else {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }

    public static void getShops(Bundle bundle, Map<String, String> headers) {
        String url = API.MAIN_URL + API.GET_ALL_SHOPS_URL;
        try {
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = JsonEngine.getStringFromJson(response, "message");
            if ("Success".equals(message)) {
                int total = JsonEngine.getIntegerFromJson(response, "total");
                bundle.putInt("total_shops", total);
                bundle.putStringArray("shops", JsonEngine.getShopsArrayFromJson(response, "shops"));

                bundle.putInt("status", RequestStatus.STATUS_OK);
            } else {
                bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }
}
