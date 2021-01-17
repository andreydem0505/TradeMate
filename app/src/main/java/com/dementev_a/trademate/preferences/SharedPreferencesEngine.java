package com.dementev_a.trademate.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesEngine {
    private final SharedPreferences sp;

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

    public void saveUser(String type, String name, String email, String accessToken) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("name", name);
        map.put("email", email);
        map.put("accessToken", accessToken);
        addManyStrings(map);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public int count() {
        return sp.getAll().size();
    }
}
