package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesPollen {

    public static void setPollen(String key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Pollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Pollen", Context.MODE_PRIVATE);
        return prefs.getString(key, "--");
    }

    public static void removePollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Pollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clearPollen(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Pollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

}
