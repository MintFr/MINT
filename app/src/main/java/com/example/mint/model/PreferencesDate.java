package com.example.mint.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class PreferencesDate {

    /**
     * TODO comment
     *
     * @param context
     */
    public static void setDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("date", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        final Calendar cldr = Calendar.getInstance();
        int year = cldr.get(Calendar.YEAR);
        int month = cldr.get(Calendar.MONTH);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        editor.putInt("year", year);
        editor.putInt("month", (month + 1));
        editor.putInt("day", day);
        editor.apply();
    }

    /**
     * this returns the last date we stored
     *
     * @param context
     * @return
     */
    public static int[] getLastDate(Context context) {
        // this is going to be used to check whether we have started a new day or month or year since the date was last saved
        SharedPreferences prefs = context.getSharedPreferences("date", Context.MODE_PRIVATE);

        // we are going to store the date as so : {day,month,year}
        int[] lastDate = new int[3];
        lastDate[0] = prefs.getInt("day", 0);
        lastDate[1] = prefs.getInt("month", 0);
        lastDate[2] = prefs.getInt("year", 0);
        return lastDate;
    }

    /**
     * TODO comment
     *
     * @return
     */
    public static int[] getCurrentDate() {
        // this doesn't use sharedPreferences but we put it here anyway to have all the time handling methods in the same place
        final Calendar cldr = Calendar.getInstance();
        int year = cldr.get(Calendar.YEAR);
        int month = cldr.get(Calendar.MONTH);
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = cldr.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cldr.getActualMaximum(Calendar.DAY_OF_MONTH);
        int[] date = {day, (month + 1), year, dayOfWeek, daysInMonth};
        return date;
    }

    /**
     * This method checks if the date is the last one in the given month, if so return false, else return true
     *
     * @param date the given date
     * @return
     */
    public static boolean checkIfSameMonth(int[] date) {
        if ((date[0] == 31 && (date[1] == 1 || date[1] == 3 || date[1] == 5 || date[1] == 7 || date[1] == 8 || date[1] == 10 || date[1] == 12))) {
            return false;
        } else if ((date[0] == 30 && (date[1] == 4 || date[1] == 6 || date[1] == 9 || date[1] == 11))) {
            return false;
        } else if (date[0] == 28 && date[1] == 2) {
            return false;
        } else {
            return true;
        }
    }
}
