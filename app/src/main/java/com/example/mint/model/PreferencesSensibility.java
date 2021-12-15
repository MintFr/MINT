package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesSensibility {

    public static void setSensibility(String key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sensibility", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSensibility(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sensibility", Context.MODE_PRIVATE);
        return prefs.getString(key, "--");
    }

    public static void removeSensibility(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sensibility", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clearSensibility(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sensibility", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

}
