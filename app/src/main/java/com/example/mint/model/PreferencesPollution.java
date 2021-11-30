package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class PreferencesPollution {

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
            // for example, for the 22 of october of 2021, the key is "22_10_2021"
            editor.putInt(i+"_"+month+"_"+PreferencesDate.getCurrentDate()[2],values.get(i-1));
        }
        editor.apply();
    }

    public static ArrayList<Integer> getPollutionMonth(int month,Context context){
        // Returns the pollution values for a month
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        ArrayList<Integer> values = new ArrayList<>();
        for (int i=1;i<=31;i++){
            values.add(prefs.getInt(i+"_"+month+"_"+PreferencesDate.getCurrentDate()[2],0));
        }
        return values;
    }

    public static void addDayPollutionToMonth(int[] date, int value, Context context){
        // adds the pollution from one day to the array of pollutions from a month
        // we are going to store the pollution from a day like so :
        // for example, for the 22 of october 2021, the key is "22_10_2021"
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        System.out.println("date"+date[0]+","+date[1]+","+date[2]);
        editor.putInt(date[0]+"_"+date[1]+"_"+date[2],value);
        editor.apply();
    }

    public static void addMonthPollutionToYear(int month, ArrayList<Integer> values, Context context){
        // this adds the pollution from one month to array of pollutions for a year
        // the pollution is stored with the same key : "day_month"
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i =1;i<=values.size();i++){
            editor.putInt(i+"_"+month+"_"+PreferencesDate.getCurrentDate()[2],values.get(i-1));
        }
        editor.apply();
    }

    public static ArrayList<Integer> getPollutionYear(int year,Context context){
        SharedPreferences prefs = context.getSharedPreferences("pollution",Context.MODE_PRIVATE);
        ArrayList<Integer> pollutionYear = new ArrayList<>();
        int[] date = {0,1,year};
        boolean sameMonth = true;
        for (int i=1;i<=12;i++){
            while (sameMonth){
                date[0]++;
                pollutionYear.add(prefs.getInt(date[0]+"_"+i+"_"+date[2],0));
                sameMonth=PreferencesDate.checkIfSameMonth(date);
            }
            sameMonth=true;
            date[0]=0;
        }
        return pollutionYear;
    }
}
