package com.dementev_a.trademate.api;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import com.dementev_a.trademate.R;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.preferences.SharedPreferencesEngine;
import com.dementev_a.trademate.requests.RequestSender;
import com.dementev_a.trademate.requests.RequestStatus;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class API {
    private static final String
            MAIN_URL = "https://trademate-api.herokuapp.com",
            AUTH_COMPANY_URL = "/auth/company",
            SIGN_UP_COMPANY_URL = "/register/company",
            AUTH_MERCHANDISER_URL = "/auth/merchandiser",
            MERCHANDISERS_URL = "/merchandisers",
            OPERATORS_URL = "/operators",
            REQUESTS_URL = "/requests",
            SHOPS_URL = "/shops",
            PHOTO_REPORTS_URL = "/photo_reports",
            PHOTO_REPORT_URL = "/report/%s",
            SEND_PHOTO_REPORT_URL = PHOTO_REPORT_URL + "/send";

    public static final String
            SUCCESS_RESPONSE = "Success";

    private final String
            ACCESS_TOKEN_KEY_HEADER = "access_token";

    public static final int
        GET_OPERATORS_HANDLER_NUMBER = 1,
        GET_MERCHANDISERS_HANDLER_NUMBER = 2,
        SIGN_UP_COMPANY_HANDLER_NUMBER = 3,
        GET_REQUESTS_HANDLER_NUMBER = 4,
        GET_SHOPS_HANDLER_NUMBER = 5,
        ADD_SHOP_HANDLER_NUMBER = 6,
        LOG_IN_COMPANY_HANDLER_NUMBER = 7,
        ADD_MERCHANDISER_HANDLER_NUMBER = 8,
        ADD_OPERATOR_HANDLER_NUMBER = 9,
        LOG_IN_MERCHANDISER_HANDLER_NUMBER = 10,
        SEND_EMAIL_HANDLER_NUMBER = 11,
        GET_PHOTO_REPORTS_HANDLER_NUMBER = 12,
        ADD_PHOTO_REPORT_HANDLER_NUMBER = 13,
        GET_PHOTOS_OF_REPORT_HANDLER = 14,
        PUT_PHOTO_HANDLER_NUMBER = 15,
        SEND_PHOTO_REPORT_TO_EMAIL_HANDLER_NUMBER = 16,
        DELETE_DIALOG_HANDLER_NUMBER = 17, // not for the API
        DELETE_PHOTO_REPORT_HANDLER_NUMBER = 18;

    private final OkHttpClient client;
    private final Context context;
    private final Handler handler;

    public API(Context context, Handler handler) {
        client = new OkHttpClient();
        this.context = context;
        this.handler = handler;
    }


    public void getOperators(String accessToken) {
        Request request = new Request.Builder()
                .url(MAIN_URL + OPERATORS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
       new RequestSender(context, client, request, handler, GET_OPERATORS_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                int total = JsonEngine.getIntegerFromJson(getStringResponse(), "total");
                getBundle().putInt(BundleEngine.TOTAL_OPERATORS_KEY_BUNDLE, total);
                getBundle().putAll(JsonEngine.getOperatorsArrayFromJson(getStringResponse(), "operators"));
            }
        }.execute();
    }

    public void getMerchandisers(String accessToken) {
        Request request = new Request.Builder()
                .url(MAIN_URL + MERCHANDISERS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
        new RequestSender(context, client, request, handler, GET_MERCHANDISERS_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                int total = JsonEngine.getIntegerFromJson(getStringResponse(), "total");
                getBundle().putInt(BundleEngine.TOTAL_MERCHANDISERS_KEY_BUNDLE, total);
                MerchandiserJson[] merchandisersArray = JsonEngine.getMerchandisersArrayFromJson(getStringResponse(), "merchandisers");
                getBundle().putParcelableArray(BundleEngine.MERCHANDISERS_KEY_BUNDLE, merchandisersArray);
            }
        }.execute();
    }

    public void getRequestsToday(String accessToken, @NotNull String... merchandiser) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Clock clock = Clock.systemUTC();
        LocalDate localDate = LocalDate.now(clock);
        String formattedDate = localDate.format(formatter);
        String url = MAIN_URL + REQUESTS_URL + "?date=" + formattedDate;
        if (merchandiser.length > 0)
            url += "&name=" + merchandiser[0];
        Request request = new Request.Builder()
                .url(url)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
        new RequestSender(context, client, request, handler, GET_REQUESTS_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                int total = JsonEngine.getIntegerFromJson(getStringResponse(), "total");
                getBundle().putInt(BundleEngine.TOTAL_REQUESTS_KEY_BUNDLE, total);
                RequestJson[] requestsArray = JsonEngine.getRequestsArrayFromJson(getStringResponse(), "requests");
                getBundle().putParcelableArray(BundleEngine.REQUESTS_KEY_BUNDLE, requestsArray);
            }
        }.execute();
    }

    public void getShops(String accessToken) {
        Request request = new Request.Builder()
                .url(MAIN_URL + SHOPS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
        new RequestSender(context, client, request, handler, GET_SHOPS_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                int total = JsonEngine.getIntegerFromJson(getStringResponse(), "total");
                getBundle().putInt(BundleEngine.TOTAL_SHOPS_KEY_BUNDLE, total);
                getBundle().putStringArray(BundleEngine.SHOPS_KEY_BUNDLE, JsonEngine.getStringArrayFromJson(getStringResponse(), "shops"));
            }
        }.execute();
    }

    public void signUpCompany(String name, String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, SIGN_UP_COMPANY_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + SIGN_UP_COMPANY_URL)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, SIGN_UP_COMPANY_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                String accessToken = JsonEngine.getStringFromJson(getStringResponse(), "accessToken");
                SharedPreferencesEngine spe = new SharedPreferencesEngine(context, context.getString(R.string.shared_preferences_user));
                spe.saveUser(context.getString(R.string.shared_preferences_type_company), name, email, accessToken, password);
            }
        }.execute();
    }

    public void addShop(String accessToken, String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_SHOP_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + SHOPS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .put(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_SHOP_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                getBundle().putString(BundleEngine.SHOP_NAME_KEY_BUNDLE, name);
            }
        }.execute();
    }

    public void logInCompany(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, LOG_IN_COMPANY_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + AUTH_COMPANY_URL)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, LOG_IN_COMPANY_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                String name = JsonEngine.getStringFromJson(getStringResponse(), "name");
                String accessToken = JsonEngine.getStringFromJson(getStringResponse(), "accessToken");
                SharedPreferencesEngine spe = new SharedPreferencesEngine(context, context.getString(R.string.shared_preferences_user));
                spe.saveUser(context.getString(R.string.shared_preferences_type_company), name, email, accessToken, password);
            }
        }.execute();
    }

    public void addMerchandiser(String accessToken, String name, String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_MERCHANDISER_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + MERCHANDISERS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .put(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_MERCHANDISER_HANDLER_NUMBER).execute();
    }

    public void addOperator(String accessToken, String name, String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_OPERATOR_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + OPERATORS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .put(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_OPERATOR_HANDLER_NUMBER).execute();
    }

    public void logInMerchandiser(String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, LOG_IN_MERCHANDISER_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + AUTH_MERCHANDISER_URL)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, LOG_IN_MERCHANDISER_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                String name = JsonEngine.getStringFromJson(getStringResponse(), "name");
                String accessToken = JsonEngine.getStringFromJson(getStringResponse(), "accessToken");
                SharedPreferencesEngine spe = new SharedPreferencesEngine(context, context.getString(R.string.shared_preferences_user));
                spe.saveUser(context.getString(R.string.shared_preferences_type_merchandiser), name, email, accessToken, password);
            }
        }.execute();
    }

    public void sendEmail(String accessToken, String shop, String text, String merchandiserName, String operatorName, String operatorEmail) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subject", shop);
            jsonObject.put("text", text + "\n" + String.format(context.getString(R.string.make_request_activity_letter_merchandiser_text), merchandiserName));
            jsonObject.put("operatorName", operatorName);
            jsonObject.put("operatorEmail", operatorEmail);
            jsonObject.put("dateTime", LocalDateTime.now().toString());
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, SEND_EMAIL_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + REQUESTS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .put(body)
                .build();
        new RequestSender(context, client, request, handler, SEND_EMAIL_HANDLER_NUMBER).execute();
    }

    public void getPhotoReports(String accessToken) {
        Request request = new Request.Builder()
                .url(MAIN_URL + PHOTO_REPORTS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
        new RequestSender(context, client, request, handler, GET_PHOTO_REPORTS_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                int total = JsonEngine.getIntegerFromJson(getStringResponse(), "total");
                getBundle().putInt(BundleEngine.TOTAL_PHOTO_REPORTS_KEY_BUNDLE, total);
                getBundle().putStringArray(BundleEngine.PHOTO_REPORTS_KEY_BUNDLE, JsonEngine.getStringArrayFromJson(getStringResponse(), "reports"));
            }
        }.execute();
    }

    public void addPhotoReport(String accessToken, String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_PHOTO_REPORT_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + PHOTO_REPORTS_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .put(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_PHOTO_REPORT_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                getBundle().putString(BundleEngine.PHOTO_REPORT_NAME_KEY_BUNDLE, name);
            }
        }.execute();
    }

    public void getPhotosOfReport(String accessToken, String name) {
        Request request = new Request.Builder()
                .url(MAIN_URL + String.format(PHOTO_REPORT_URL, name))
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
        new RequestSender(context, client, request, handler, GET_PHOTOS_OF_REPORT_HANDLER) {
            @Override
            public void successMessage() {
                int total = JsonEngine.getIntegerFromJson(getStringResponse(), "total");
                getBundle().putInt(BundleEngine.TOTAL_PHOTOS_KEY_BUNDLE, total);
                getBundle().putAll(JsonEngine.getPhotosFromJson(getStringResponse(), "bytes"));
            }
        }.execute();
    }

    public void putPhoto(String accessToken, byte[] byteCode, String photoReportName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("byteCode", Base64.encodeToString(byteCode, Base64.CRLF));
        } catch (JSONException e) {
            Bundle bundle = new Bundle();
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, PUT_PHOTO_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + String.format(PHOTO_REPORT_URL, photoReportName))
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .put(body)
                .build();
        new RequestSender(context, client, request, handler, PUT_PHOTO_HANDLER_NUMBER).execute();
    }

    public void sendPhotoReport(String accessToken, String name) {
        Request request = new Request.Builder()
                .url(MAIN_URL + String.format(SEND_PHOTO_REPORT_URL, name))
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .build();
        new RequestSender(context, client, request, handler, SEND_PHOTO_REPORT_TO_EMAIL_HANDLER_NUMBER).execute();
    }

    public void deletePhotoReport(String accessToken, String name) {
        Request request = new Request.Builder()
                .url(MAIN_URL + String.format(PHOTO_REPORT_URL, name))
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .delete()
                .build();
        new RequestSender(context, client, request, handler, DELETE_PHOTO_REPORT_HANDLER_NUMBER).execute();
    }
}
