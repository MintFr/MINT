package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.YearMonth;
import java.util.ArrayList;

public class PreferencesPollution2 {
    public static void setPollutionToday(int value, Context context) {
        // Stores the pollution from today
        SharedPreferences prefs = context.getSharedPreferences("pollution", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PreferencesDate2.getCurrentDate(), value);
        editor.apply();
    }

    public static int getPollutionToday(Context context) {
        // Returns the pollution from today
        SharedPreferences prefs = context.getSharedPreferences("pollution", Context.MODE_PRIVATE);
        int pollutionToday = prefs.getInt(PreferencesDate2.getCurrentDate(), 0);
        return pollutionToday;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Integer> getPollutionMonth(int month, int year, Context context) {
        // Returns the pollution values for a month
        // Get the number of days in that month
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        SharedPreferences prefs = context.getSharedPreferences("pollution", Context.MODE_PRIVATE);
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            values.add(prefs.getInt(i + "_" + month + "_" + year, 0));
        } return values;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Integer> getPollutionYear(int year, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pollution", Context.MODE_PRIVATE);
        ArrayList<Integer> pollutionYear = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            YearMonth yearMonthObject = YearMonth.of(year, i);
            int daysInMonth = yearMonthObject.lengthOfMonth();
            for (int j=1;j<=daysInMonth;j++){
                pollutionYear.add(prefs.getInt(j + "_" + i + "_" + year, 0));
            }
        }
        return pollutionYear;
    }
}
