package com.dementev_a.trademate.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesEngine {
    private final SharedPreferences sp;

    public static final String
        TYPE_KEY = "type",
        NAME_KEY = "name",
        EMAIL_KEY = "email",
        ACCESS_TOKEN_KEY = "accessToken",
        PASSWORD_KEY = "password",
        KEYWORD_KEY = "keyword";

    public SharedPreferencesEngine(@NotNull Context context, String source) {
        sp = context.getSharedPreferences(source, Context.MODE_PRIVATE);
    }

    private void addManyStrings(Map<String, String> map) {
        SharedPreferences.Editor editor = sp.edit();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            editor.putString(key, map.get(key));
        }
        editor.apply();
    }

    public void saveUser(String type, String name, String email, String accessToken, String password) {
        Map<String, String> map = new HashMap<>();
        map.put(TYPE_KEY, type);
        map.put(NAME_KEY, name);
        map.put(EMAIL_KEY, email);
        map.put(ACCESS_TOKEN_KEY, accessToken);
        map.put(PASSWORD_KEY, password);
        addManyStrings(map);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean containsKey(String key) {
        return sp.contains(key);
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public int count() {
        return sp.getAll().size();
    }
}
