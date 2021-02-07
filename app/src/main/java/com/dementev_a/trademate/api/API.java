package com.dementev_a.trademate.api;

import android.os.Bundle;

import com.dementev_a.trademate.LogInCompanyActivity;
import com.dementev_a.trademate.R;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.OperatorJson;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestEngine;

import java.io.IOException;
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
            ALL_OPERATORS_URL = "/operators";


    // using several times
    public static void getOperators(Bundle bundle, Map<String, String> headers) {
        String url = API.MAIN_URL + API.ALL_OPERATORS_URL;
        try {
            JsonEngine jsonEngine = new JsonEngine();
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = jsonEngine.getStringFromJson(response, "message");
            switch (message) {
                case "Success": {
                    int total = jsonEngine.getIntegerFromJson(response, "total");
                    bundle.putInt("total_operators", total);

                    OperatorJson[] operatorsArray = jsonEngine.getOperatorsArrayFromJson(response, "operators");
                    bundle.putParcelableArray("operators", operatorsArray);

                    bundle.putInt("status", RequestStatus.STATUS_OK);
                } break;
                case "Access token is wrong": {
                    bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
                } break;
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }

    public static void getMerchandisers(Bundle bundle, Map<String, String> headers) {
        String url = API.MAIN_URL + API.ALL_MERCHANDISERS_URL;
        try {
            JsonEngine jsonEngine = new JsonEngine();
            String response = RequestEngine.makeGetRequest(url, headers);
            String message = jsonEngine.getStringFromJson(response, "message");
            switch (message) {
                case "Success": {
                    int total = jsonEngine.getIntegerFromJson(response, "total");
                    bundle.putInt("total_merchandisers", total);

                    MerchandiserJson[] merchandisersArray = jsonEngine.getMerchandisersArrayFromJson(response, "merchandisers");
                    bundle.putParcelableArray("merchandisers", merchandisersArray);
                } break;
                case "Access token is wrong": {
                    bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
                }
            }
        } catch (IOException e) {
            bundle.putInt("status", RequestStatus.STATUS_SERVER_ERROR);
        }
    }
}
