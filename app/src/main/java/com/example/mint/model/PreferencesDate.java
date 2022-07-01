package com.example.mint.model;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class PreferencesDate {
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat formatDate = new SimpleDateFormat("d_M_yy");

    /**
     * Method to get the current date on the format "d_M_yy" (7_2_22) for the 7th of february 2022
     * @return String date
     */
    public static String getCurrentDate() {
        final Calendar cldr = Calendar.getInstance();
        final String date = formatDate.format(cldr.getTime());
        return date;
    }

}


