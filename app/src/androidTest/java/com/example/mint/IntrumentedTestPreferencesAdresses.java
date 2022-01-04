package com.example.mint;


import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.example.mint.controller.ProfileActivity;
import com.example.mint.model.PreferencesAddresses;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntrumentedTestPreferencesAdresses {
    Context context =   ApplicationProvider.getApplicationContext();
    @Before
    public void setup(){
        PreferencesAddresses.clearAddresses(context);
        PreferencesAddresses.addAddress("Address",1,"ecole", context);
    }

    @Test
    public void testGetAddress(){

        assertTrue(PreferencesAddresses.getAddress("startAddress",context)=="ecole");
    }
    @After
    public void setDown(){
        PreferencesAddresses.clearAddresses(context);
    }

}
