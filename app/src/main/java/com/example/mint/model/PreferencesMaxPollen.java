package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesMaxPollen {

    public static void setMaxPollen(String key, int value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getMaxPollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        return prefs.getInt(key, 10);
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