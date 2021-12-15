package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class PreferencesTransport {

    public static void addTransportation(String arrayName, int key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(arrayName + "_" + key, value);
        editor.apply();
    }

    public static void removeTransportation(String arrayName, int key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(arrayName + "_" + key);
        editor.apply();
    }

    public static void clearTransportation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static ArrayList<String> getPrefTransportation(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        ArrayList<String> array = new ArrayList<>(4);
        for (int i = 0; i < 4; i++)
            array.add(prefs.getString(arrayName + "_" + i, "--"));
        return array;
    }

    public static int getNumberOfTransportation(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        return prefs.getInt(arrayName + "_size", 0);
    }

    ////OPTION//////

    public static void setOptionTransportation(int[] value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("optionTransport", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < 4; i++) {
            editor.putInt("option_" + (i + 1), value[i]);
        }
        editor.apply();
    }

    public static void addOptionTransportation(String key, int value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("optionTransport", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("option_" + key, value);
        editor.apply();
    }

    public static int[] getOptionTransportation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("optionTransport", Context.MODE_PRIVATE);
        int[] options = new int[4];
        for (int i = 0; i < 4; i++) {
            options[i] = prefs.getInt("option_" + (i + 1), 0);
        }
        return options;
    }

    public static int getNumberItinerary(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("optionTransport", Context.MODE_PRIVATE);
        int number = 0;
        for (int i = 0; i < 4; i++) {
            if (prefs.getInt("option_" + (i + 1), 0) != 0) {
                number++;
            }
        }
        return number;
    }

    public static void clearOptionTransportation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("optionTransport", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
