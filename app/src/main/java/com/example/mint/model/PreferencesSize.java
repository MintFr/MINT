package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesSize {
    public static void setSize(String key, String value, Context context) {
        SharedPreferences pref = context.getSharedPreferences("police", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSize(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences("police", Context.MODE_PRIVATE);
        return pref.getString(key, "normal");
    }

    public static void removeSize(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences("police", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }


}
