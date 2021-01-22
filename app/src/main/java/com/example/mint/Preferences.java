package com.example.mint;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class is used to record the user's preferences in terms of sensibility, addresses and transportation
 */
public class Preferences {

    // TIME HANDLING //
    // this will be used to handle the storage of pollution over different days and months, to check when a new day/month/year starts

    /**
     * TODO comment
     * @param context
     */
    public static void setDate(Context context){
        SharedPreferences prefs = context.getSharedPreferences("date",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        final Calendar cldr = Calendar.getInstance();
        int year = cldr.get(Calendar.YEAR);
        int month = cldr.get(Calendar.MONTH);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        editor.putInt("year",year);
        editor.putInt("month",month);
        editor.putInt("day",day);
        editor.apply();
    }

    /**
     * this returns the last date we stored
     * @param context
     * @return
     */
    public static int[] getLastDate(Context context){
        // this is going to be used to check whether we have started a new day or month or year since the date was last saved
        SharedPreferences prefs = context.getSharedPreferences("date",Context.MODE_PRIVATE);

        // we are going to store the date as so : {day,month,year}
        int[] lastDate = new int[3];
        lastDate[0] = prefs.getInt("day",0);
        lastDate[1] = prefs.getInt("month",0);
        lastDate[2] = prefs.getInt("year",0);
        return lastDate;
    }

    /**
     * TODO comment
     * @return
     */
    public static int[] getCurrentDate(){
        // this doesn't use sharedPreferences but we put it here anyway to have all the time handling methods in the same place
        final Calendar cldr = Calendar.getInstance();
        int year = cldr.get(Calendar.YEAR);
        int month = cldr.get(Calendar.MONTH);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int[] date = {day,month,year};
        return date;
    }

    //////////////////////////////
    // POLLUTION

    /**
     * TODO comment
     * @param value
     * @param context
     */
    public static void setLastPollution(int value, Context context){
        // Stores the pollution from the last itinerary
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("lastPollution",value);
        editor.apply();
    }
    public static int getLastPollution(Context context){
        // Returns the pollution from the last itinerary
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        int lastPollution = prefs.getInt("lastPollution",0);
        return lastPollution;
    }
    public static void setPollutionToday(int value, Context context){
        // Stores the pollution from today
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("pollutionToday",value);
        editor.apply();
    }
    public static int getPollutionToday(Context context){
        // Returns the pollution from today
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        int pollutionToday = prefs.getInt("pollutionToday",0);
        return pollutionToday;
    }
    public static void setPollutionMonth(int month, ArrayList<Integer> values, Context context){
        // Stores the pollution from this month
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i=1;i<=values.size();i++){
            // we are going to store the pollution from a day like so :
            // for example, for the 22 of october, the key is "22_10"
            editor.putInt(i+"_"+month,values.get(i-1));
        }
        editor.apply();
    }
    public static ArrayList<Integer> getPollutionMonth(int month,Context context){
        // Returns the pollution values for a month
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        ArrayList<Integer> values = new ArrayList<>();
        for (int i=1;i<=31;i++){
            values.add(prefs.getInt(i+"_"+month,0));
        }
        return values;
    }
    public static void addDayPollutionToMonth(int[] date, int value, Context context){
        // adds the pollution from one day to the array of pollutions from a month
        // we are going to store the pollution from a day like so :
        // for example, for the 22 of october, the key is "22_10"
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(date[0]+"_"+date[1],value);
        editor.apply();
    }

    /////////////////////
    //  OPTION
    public static void setOptionTransportation(int[] value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("optionTransport",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i=1;i<=4;i++){
            editor.putInt("option_"+i, value[i]);
        }
        editor.apply();
    }
    public static void addOptionTransportation(String key, int value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("optionTransport",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("option_"+key,value);
        editor.apply();
    }
    public static int[] getOptionTransportation(Context context){
        SharedPreferences prefs = context.getSharedPreferences("optionTransport",Context.MODE_PRIVATE);
        int[] options = new int[4];
        for (int i=0;i<4;i++){
            options[i]=prefs.getInt("option_"+(i+1),0);
        }
        return options;
    }
    public static int getNumberItinerary(Context context){
        SharedPreferences prefs = context.getSharedPreferences("optionTransport",Context.MODE_PRIVATE);
        int number=0;
        for (int i=0;i<4;i++){
            if (prefs.getInt("option_"+(i+1),0)!=0){
                number++;
            }
        }
        return number;
    }
    public static void clearOptionTransportation(Context context){
        SharedPreferences prefs = context.getSharedPreferences("optionTransport",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    //  OPTION END //

    // for the itinerary
    public static void addAddress(String key, String value, Context context){
        SharedPreferences prefs = context.getSharedPreferences("startEndAddress",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    // for the itinerary
    public static String getAddress(String key, Context context){
        SharedPreferences prefs = context.getSharedPreferences("startEndAddress", Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

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

    // END TRANSPORTATION //
}
