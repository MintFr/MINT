package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesMaxPollen {
    /**
     * Put in memory the max value of the pollen, used to change the color of the button depending on the maximum value and the sensitivity of the user
     * @param key name of the key used to get the value
     * @param value maximum value for the pollens
     * @param context
     */
    public static void setMaxPollen(String key, int value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Returns the maximum values of the pollen stored previously
     * @param key name of the key used to get the value (same as the one in the setMaxPollen)
     * @param context
     * @return maximum value for a pollen stored
     */
    public static int getMaxPollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        return prefs.getInt(key, 10);
    }

    /**
     * Deletes a value in the maxPollen Shared Preferences according to the key
     * @param key key of the value to delete
     * @param context
     */
    public static void removeMaxPollen(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Clears the whole maxPollen Shared Preferences
     * @param context
     */
    public static void clearMaxPollen(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("maxPollen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}