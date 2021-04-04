package com.dementev_a.trademate.api;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;

import com.dementev_a.trademate.R;
import com.dementev_a.trademate.bundle.BundleEngine;
import com.dementev_a.trademate.json.JsonEngine;
import com.dementev_a.trademate.json.MerchandiserJson;
import com.dementev_a.trademate.json.RequestJson;
import com.dementev_a.trademate.messages.EmailSending;
import com.dementev_a.trademate.messages.MessageSender;
import com.dementev_a.trademate.messages.StrategyMessage;
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
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
            ADD_SHOP_URL = "/create/shop",
            GET_PHOTO_REPORTS_URL = "/photo_reports";

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
        GET_PHOTO_REPORTS_HANDLER_NUMBER = 12;

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
                .url(MAIN_URL + ALL_OPERATORS_URL)
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
                .url(MAIN_URL + ALL_MERCHANDISERS_URL)
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
        String url = MAIN_URL + GET_ALL_REQUESTS_URL + "?date=" + formattedDate;
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
                .url(MAIN_URL + GET_ALL_SHOPS_URL)
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

    public void signUpCompany(EditText nameET, EditText emailET, EditText passwordET) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, SIGN_UP_COMPANY_HANDLER_NUMBER);
            return;
        }
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
        } catch (JSONException e) {
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
                spe.saveUser(context.getString(R.string.shared_preferences_type_company), name, email, accessToken);
            }
        }.execute();
    }

    public void addShop(String accessToken, EditText nameET) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(nameET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_SHOP_HANDLER_NUMBER);
            return;
        }
        String name = nameET.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_SHOP_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + ADD_SHOP_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_SHOP_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                getBundle().putString(BundleEngine.SHOP_NAME_KEY_BUNDLE, name);
            }
        }.execute();
    }

    public void logInCompany(EditText emailET, EditText passwordET) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, LOG_IN_COMPANY_HANDLER_NUMBER);
            return;
        }
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
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
                spe.saveUser(context.getString(R.string.shared_preferences_type_company), name, email, accessToken);
            }
        }.execute();
    }

    public void addMerchandiser(String accessToken, EditText nameET, EditText emailET, EditText passwordET) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(passwordET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_MERCHANDISER_HANDLER_NUMBER);
            return;
        }
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_MERCHANDISER_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + ADD_MERCHANDISER_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_MERCHANDISER_HANDLER_NUMBER).execute();
    }

    public void addOperator(String accessToken, EditText nameET, EditText emailET) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(emailET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_OPERATOR_HANDLER_NUMBER);
            return;
        }
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, ADD_OPERATOR_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + ADD_OPERATOR_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, ADD_OPERATOR_HANDLER_NUMBER).execute();
    }

    public void logInMerchandiser(EditText emailET, EditText passwordET) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(emailET.getText()) || TextUtils.isEmpty(emailET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, LOG_IN_MERCHANDISER_HANDLER_NUMBER);
            return;
        }
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (JSONException e) {
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
                spe.saveUser(context.getString(R.string.shared_preferences_type_merchandiser), name, email, accessToken);
            }
        }.execute();
    }

    public void sendEmail(String accessToken, EditText nameET, EditText textET, String merchandiserName, int operatorPosition, String[] shops, String[] namesOfOperators, String[] emailsOfOperators) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(nameET.getText()) || TextUtils.isEmpty(textET.getText())) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_EMPTY_FIELDS);
            new RequestSender().sendHandlerMessage(bundle, handler, SEND_EMAIL_HANDLER_NUMBER);
            return;
        }
        String shop = nameET.getText().toString();
        String text = textET.getText().toString();
        if (Arrays.binarySearch(shops, shop) < 0) {
            BundleEngine.putError(bundle, R.string.make_request_activity_shop_was_not_found_error_text);
            new RequestSender().sendHandlerMessage(bundle, handler, SEND_EMAIL_HANDLER_NUMBER);
            return;
        }
        String operatorName = namesOfOperators[operatorPosition];
        String operatorEmail = emailsOfOperators[operatorPosition];
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subject", shop);
            jsonObject.put("text", text);
            jsonObject.put("operator", operatorName);
            jsonObject.put("dateTime", LocalDateTime.now().toString());
        } catch (JSONException e) {
            bundle.putInt(BundleEngine.STATUS_KEY_BUNDLE, RequestStatus.STATUS_SERVER_ERROR);
            new RequestSender().sendHandlerMessage(bundle, handler, SEND_EMAIL_HANDLER_NUMBER);
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Request request = new Request.Builder()
                .url(MAIN_URL + CREATE_REQUEST_URL)
                .header(ACCESS_TOKEN_KEY_HEADER, accessToken)
                .post(body)
                .build();
        new RequestSender(context, client, request, handler, SEND_EMAIL_HANDLER_NUMBER) {
            @Override
            public void successMessage() {
                MessageSender sender = new MessageSender();
                sender.setMethod(new EmailSending());
                sender.send(new StrategyMessage(operatorEmail, shop, text, String.format(context.getString(R.string.make_request_activity_letter_merchandiser_text), merchandiserName)));
            }
        }.execute();
    }

    public void getPhotoReports(String accessToken) {
        Request request = new Request.Builder()
                .url(MAIN_URL + GET_PHOTO_REPORTS_URL)
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
}
