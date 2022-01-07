package com.example.mint;


import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.example.mint.controller.ProfileActivity;
import com.example.mint.model.Address;
import com.example.mint.model.PreferencesAddresses;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntrumentedTestPreferencesAdresses {
    Context context =   ApplicationProvider.getApplicationContext();
    @Before
    public void setup(){
        PreferencesAddresses.clearAddresses(context);
        PreferencesAddresses.addAddress("Address",0,"ecole", context);
        PreferencesAddresses.clearAddressItinerary(context);
    }

    @Test
    public void testGetPrefAddress(){

        assertTrue(PreferencesAddresses.getPrefAddresses("Address",context).get(0)=="ecole");
    }

    @Test
    public void testGetNumberOfAddress(){

        assertTrue(PreferencesAddresses.getNumberOfAddresses("Address",context)==1);
    }
    @Test
    public void testAddGetAddressItinerary(){
        PreferencesAddresses.addAddress("StartAddress","ecle",context);
        Log.d("testAddGetAddressItinerary",PreferencesAddresses.getAddress("StartAddress",context));
        assertTrue(PreferencesAddresses.getAddress("StartAddress",context)=="ecle");
    }

    @Test
    public void testRemoveAddress(){
        PreferencesAddresses.addAddress("Address",1,"caca", context);
        PreferencesAddresses.removeAddress("Address",1,context);
        assertTrue(PreferencesAddresses.getNumberOfAddresses("Address",context)==1);

    }
    @After
    public void setDown(){
        PreferencesAddresses.clearAddresses(context);
    }

}
