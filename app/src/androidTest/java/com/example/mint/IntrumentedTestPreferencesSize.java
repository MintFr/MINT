package com.example.mint;


import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import com.example.mint.model.PreferencesSize;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntrumentedTestPreferencesSize {
    Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void setup() {
        SharedPreferences pref = context.getSharedPreferences("police", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("police");
        editor.apply();


    }

    @Test
    public void testGetSize() {

        assertEquals(PreferencesSize.getSize("police", context), "normal");

    }


    @After
    public void setDown() {
        PreferencesSize.setSize("police", "normal", context);

    }

}
