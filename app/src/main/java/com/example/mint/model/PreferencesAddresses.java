package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class PreferencesAddresses {

    // for the itinerary

    /**
     * Adds an address in memory in the startEndAddress shared preferences, used to transfer the value in the search bar to the itinerary activity and server request
     * @param key key to get back the value (start or end generally)
     * @param value name of the address
     * @param context
     */
    public static void addAddress(String key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("startEndAddress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    /**
     * Gets an address from memory in the startEndAddress shared preferences, used to transfer the value in the search bar to the itinerary activity and server request
     * @param key key of the value we want (start or end generally)
     * @param context
     * @return name of the address
     */
    // for the itinerary
    public static String getAddress(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("startEndAddress", Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    /**
     * Clears the whole startEndAddress shared preferences to ensure no overlap
     * @param context
     */
    public static void clearAddressItinerary(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("startEndAddress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    // ADDRESSES //

    public static void setPrefAddresses(String arrayName, ArrayList<String> array, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.size());
        for (int i = 0; i < array.size(); i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }

    /**
     * returns the list of preferred addresses
     * @param arrayName the name of the array in which are saved the addresses
     * @param context
     * @return list of addresses
     */
    public static ArrayList<String> getPrefAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }

    /**
     * get the number of addresses in the array
     * @param arrayName the name of the array we want the addresses in
     * @param context
     * @return int number of addresses
     */
    public static int getNumberOfAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        int numberOfAddresses = prefs.getInt(arrayName + "_size", 0);
        return numberOfAddresses;
    }

    /**
     * Remove one address in the array
     * @param arrayName name of the array in which we want to delete an address
     * @param ind index of the address to remove
     * @param context
     */
    public static void removeAddress(String arrayName, int ind, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int newSize = prefs.getInt(arrayName + "_size", 0) - 1;
        for (int i = ind; i < newSize; i++) {
            editor.putString(arrayName + "_" + i, prefs.getString(arrayName + "_" + (i + 1), null));
        }
        editor.remove(arrayName + "_" + (newSize));
        editor.putInt(arrayName + "_size", newSize);
        editor.apply();
    }

    /**
     * Add one address in the chosen array
     * @param arrayName name of the array in which we want to add an address
     * @param key index of the address to add
     * @param value name of the address we want to add
     * @param context
     */
    public static void addAddress(String arrayName, int key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(arrayName + "_" + key, value);
        int newSize = (prefs.getInt(arrayName + "_size", 0)) + 1;
        editor.putInt(arrayName + "_size", newSize);
        editor.apply();
    }

    /**
     * Delete all addresses in memory for the preferred address
     * @param context
     */
    public static void clearAddresses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // History

    /**
     * returns the list of last addresses
     * @param arrayName
     * @param context
     * @return array of the last address
     */
    public static ArrayList<String> getLastAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }

    /**
     * Add one address in the chosen array (lastAddress generally)
     * @param arrayName name of the array in which we want to add an address
     * @param key index of the address to add
     * @param value name of the address we want to add
     * @param context
     */
    public static void addLastAddress(String arrayName, int key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int newSize = (prefs.getInt(arrayName + "_size", 0)) + 1;
        editor.putInt(arrayName + "_size", newSize);
        for (int i = newSize - 2; i >= key; i--) {
            editor.putString(arrayName + "_" + (i + 1), prefs.getString(arrayName + "_" + i, null));
        }
        editor.putString(arrayName + "_" + key, value);
        editor.apply();
    }

    /**
     * Remove one address in the array
     * @param arrayName name of the array in which we want to delete an address
     * @param key index of the address to remove
     * @param context
     */
    public static void removeLastAddress(String arrayName, int key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(arrayName + "_" + key);
        int newSize = (prefs.getInt(arrayName + "_size", 0)) - 1;
        for (int i = key; i < newSize; i++) {
            editor.putString(arrayName + "_" + i, prefs.getString(arrayName + "_" + (i + 1), null));
        }
        editor.putInt(arrayName + "_size", newSize);
        editor.apply();
    }

    /**
     * get the number of addresses in the array last address
     * @param arrayName the name of the array we want the addresses in
     * @param context
     * @return int number of addresses
     */
    public static int getNumberOfLastAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        int numberOfAddresses = prefs.getInt(arrayName + "_size", 0);
        return numberOfAddresses;
    }

    /**
     * Moves one address to be the first one in the last address array
     * @param key
     * @param context
     */
    public static void moveAddressFirst(int key, Context context) {
        String movedAddress = getLastAddresses("lastAddress", context).get(key);
        removeLastAddress("lastAddress", key, context);
        addLastAddress("lastAddress", 0, movedAddress, context);
    }

    /**
     * Clear the last address array
     * @param context
     */
    public static void clearLastAddresses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
