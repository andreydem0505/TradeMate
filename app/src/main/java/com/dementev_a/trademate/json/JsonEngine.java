package com.dementev_a.trademate.json;

import android.os.Bundle;
import android.util.Base64;

import com.dementev_a.trademate.bundle.BundleEngine;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonEngine {

    public static String getStringFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.get(arg).getAsString();
    }

    public static int getIntegerFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.get(arg).getAsInt();
    }

    public static String[] getStringArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        String[] result = new String[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).getAsString();
        }
        return result;
    }

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

    public static RequestJson[] getRequestsArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        RequestJson[] requestsArray = new RequestJson[array.size()];
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            requestsArray[i] = new RequestJson(jo.get("subject").getAsString(), jo.get("text").getAsString(), jo.get("operatorName").getAsString(), jo.get("from").getAsString(), jo.get("dateTime").getAsString());
        }
        return requestsArray;
    }

    public static Bundle getPhotosFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        Bundle bundle = new Bundle();
        for (int i = 0; i < array.size(); i++) {
            String byteCodeString = array.get(i).getAsString();
            byte[] byteCode = Base64.decode(byteCodeString, Base64.CRLF);
            bundle.putByteArray("photo" + i, byteCode);
        }
        return bundle;
    }
}
