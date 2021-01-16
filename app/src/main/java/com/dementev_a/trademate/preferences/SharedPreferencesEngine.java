package com.dementev_a.trademate.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

public class SharedPreferencesEngine {
    private final SharedPreferences sp;

    public SharedPreferencesEngine(@NotNull Context context, String source) {
        sp = context.getSharedPreferences(source, Context.MODE_PRIVATE);
    }

    private void addManyStrings(String[] key, String[] value) {
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < key.length; i++) {
            editor.putString(key[i], value[i]);
        }
        editor.apply();
    }

    public void saveUser(String type, String name, String email, String accessToken) {
        addManyStrings(
                new String[]{"type", "name", "email", "accessToken"},
                new String[]{type, name, email, accessToken}
                );
    }

    public int count() {
        return sp.getAll().size();
    }
}
