package com.dementev_a.trademate.json;

import com.google.gson.Gson;
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
}
