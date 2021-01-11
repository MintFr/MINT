package com.example.mint;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

/**
 * This class is used to record the user's preferences in terms of sensibility, addresses and transportation
 */
public class Preferences {

    // SENSIBILITY //
    public static void setSensibility(String key, String value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("sensibility",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSensibility(String key, Context context){
        SharedPreferences prefs = context.getSharedPreferences("sensibility", Context.MODE_PRIVATE);
        return prefs.getString(key, "--");
    }

    public static void removeSensibility(String key, Context context){
        SharedPreferences prefs = context.getSharedPreferences("sensibility",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clearSensibility(Context context){
        SharedPreferences prefs = context.getSharedPreferences("sensibility", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // END SENSIBILITY//

    // ADDRESSES //

    public static void setPrefAddresses(String arrayName, ArrayList<String> array, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }

    public static ArrayList<String> getPrefAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for(int i=0;i<size;i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }

    public static int getNumberOfAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        int numberOfAddresses = prefs.getInt(arrayName + "_size", 0);
        return numberOfAddresses;
    }

    public static void removeAddress(String arrayName, int ind, Context context){
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int newSize = prefs.getInt(arrayName+"_size",0)-1;
        for(int i=ind;i<newSize;i++) {
            editor.putString(arrayName + "_" + i, prefs.getString(arrayName + "_" + (i + 1), null));
        }
        editor.remove(arrayName + "_" + (newSize));
        editor.putInt(arrayName+"_size",newSize);
        editor.apply();
    }

    public static void addAddress(String arrayName, int key, String value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(arrayName+"_"+key,value);
        int newSize = (prefs.getInt(arrayName+"_size",0))+1;
        editor.putInt(arrayName+"_size",newSize);
        editor.apply();
    }
    public static void clearAddresses(Context context){
        SharedPreferences prefs = context.getSharedPreferences("listOfAddresses", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // History

    public static ArrayList<String> getLastAddresses(String arrayName, Context context){
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for(int i=0;i<size;i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }
    public static void addLastAddress(String arrayName, int key, String value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("lastAddress",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int newSize = (prefs.getInt(arrayName+"_size",0))+1;
        editor.putInt(arrayName+"_size",newSize);
        for(int i=newSize-2;i>=key;i--) {
            editor.putString(arrayName + "_" + (i+1), prefs.getString(arrayName + "_" + i, null));
        }
        editor.putString(arrayName+"_"+key,value);
        editor.apply();
    }
    public static void removeLastAddress(String arrayName, int key, Context context){
        SharedPreferences prefs = context.getSharedPreferences("lastAddress",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(arrayName + "_" + key);
        int newSize = (prefs.getInt(arrayName+"_size",0))-1;
        for(int i=key;i<newSize;i++) {
            editor.putString(arrayName + "_" + i, prefs.getString(arrayName + "_" + (i + 1), null));
        }
        editor.putInt(arrayName+"_size",newSize);
        editor.apply();
    }
    public static int getNumberOfLastAddresses(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        int numberOfAddresses = prefs.getInt(arrayName + "_size", 0);
        return numberOfAddresses;
    }

    public static void moveAddressFirst(int key, Context context){
        String movedAddress = getLastAddresses("lastAddress",context).get(key);
        removeLastAddress("lastAddress",key,context);
        addLastAddress("lastAddress",0,movedAddress,context);
    }

    public static void clearLastAddresses(Context context){
        SharedPreferences prefs = context.getSharedPreferences("lastAddress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // END ADDRESSES //

    // TRANSPORTATION //

    public static void addTransportation(String arrayName, int key, String value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("Transportation",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(arrayName+"_"+key, value);
        editor.apply();
    }

    public static void removeTransportation(String arrayName, int key, Context context){
        SharedPreferences prefs = context.getSharedPreferences("Transportation",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(arrayName + "_" + key);
        editor.apply();
    }

    public static void clearTransportation(Context context){
        SharedPreferences prefs = context.getSharedPreferences("Transportation",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static ArrayList<String> getPrefTransportation(String arrayName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        ArrayList<String> array = new ArrayList<>(4);
        for(int i=0;i<4;i++)
            array.add(prefs.getString(arrayName + "_" + i, "--"));
        return array;
    }

    public static int getNumberOfTransportation(String arrayName, Context context){
        SharedPreferences prefs = context.getSharedPreferences("Transportation", Context.MODE_PRIVATE);
        return prefs.getInt(arrayName + "_size", 0);
    }

    // END TRANPORTATION //
}
