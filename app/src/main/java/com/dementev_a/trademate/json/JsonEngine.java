package com.dementev_a.trademate.json;

import android.os.Bundle;
import android.util.Base64;

import com.dementev_a.trademate.bundle.BundleEngine;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

public class JsonEngine {
    // Depend on API

    public static String getStringFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.get(arg).getAsString();
    }

    public static int getIntegerFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.get(arg).getAsInt();
    }

    @NotNull
    public static String[] getStringArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        String[] result = new String[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).getAsString();
        }
        return result;
    }

    public static String getInputError(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        jo = array.get(0).getAsJsonObject();
        return jo.get("message").getAsString();
    }
    /* Example
    {
        "violations": [
            {
                "fieldName": "password",
                "message": "Password is unreliable"
            },
            {
                "fieldName": "email",
                "message": "Email is incorrect"
            }
        ]
    }
    */

    @NotNull
    public static MerchandiserJson[] getMerchandisersArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        MerchandiserJson[] merchandisersArray = new MerchandiserJson[array.size()];
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            merchandisersArray[i] = new MerchandiserJson(jo.get("name").getAsString(), jo.get("email").getAsString(), jo.get("password").getAsString());
        }
        return merchandisersArray;
    }

    @NotNull
    public static Bundle getOperatorsArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        String[] names = new String[array.size()];
        String[] emails = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            names[i] = jo.get("name").getAsString();
            emails[i] = jo.get("email").getAsString();
        }
        Bundle bundle = new Bundle();
        bundle.putStringArray(BundleEngine.NAMES_OF_OPERATORS_KEY_BUNDLE, names);
        bundle.putStringArray(BundleEngine.EMAILS_OF_OPERATORS_KEY_BUNDLE, emails);
        return bundle;
    }

    @NotNull
    public static RequestJson[] getRequestsArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        RequestJson[] requestsArray = new RequestJson[array.size()];
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            requestsArray[i] = new RequestJson(jo.get("subject").getAsString(), jo.get("text").getAsString(), jo.get("operatorName").getAsString(), jo.get("dateTime").getAsString());
        }
        return requestsArray;
    }

    @NotNull
    public static Bundle getPhotosFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        Bundle bundle = new Bundle();
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            String byteCodeString = jo.get("bytecode").getAsString();
            long id = jo.get("id").getAsLong();
            byte[] byteCode = Base64.decode(byteCodeString, Base64.CRLF);
            bundle.putByteArray("photo" + i, byteCode);
            bundle.putLong("id" + i, id);
        }
        return bundle;
    }
}
