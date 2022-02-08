package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesMaxPollen {

    public static void setMaxPollen(String key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getMaxPollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        return prefs.getString(key, "--");
    }

    public static void removeMaxPollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clearMaxPollen(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}