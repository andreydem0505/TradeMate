package com.dementev_a.trademate.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonEngine {
    private final Gson gson;

    public JsonEngine() {
        gson = new Gson();
    }

    public String getStringFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.get(arg).getAsString();
    }

    public int getIntegerFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.get(arg).getAsInt();
    }

    public MerchandiserJson[] getMerchandisersArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        MerchandiserJson[] merchandisersArray = new MerchandiserJson[array.size()];
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            merchandisersArray[i] = new MerchandiserJson(jo.get("name").getAsString(), jo.get("email").getAsString());
        }
        return merchandisersArray;
    }

    public OperatorJson[] getOperatorsArrayFromJson(String json, String arg) {
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        JsonArray array = jo.get(arg).getAsJsonArray();
        OperatorJson[] operatorsArray = new OperatorJson[array.size()];
        for (int i = 0; i < array.size(); i++) {
            jo = array.get(i).getAsJsonObject();
            operatorsArray[i] = new OperatorJson(jo.get("name").getAsString(), jo.get("email").getAsString());
        }
        return operatorsArray;
    }
}
