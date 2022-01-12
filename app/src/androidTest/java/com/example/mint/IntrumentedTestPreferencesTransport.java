package com.example.mint;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.mint.model.PreferencesTransport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class IntrumentedTestPreferencesTransport {
    Context context = ApplicationProvider.getApplicationContext();
    int[] fav = {1, 0, 3, 0};

    @Before
    public void setup() {
        PreferencesTransport.clearTransportation(context);
        PreferencesTransport.addTransportation("Transportation", 0, "velo", context);
        PreferencesTransport.addTransportation("Transportation", 1, "voiture", context);
        PreferencesTransport.addTransportation("Transportation", 2, "pied", context);
        PreferencesTransport.addTransportation("Transportation", 3, "transport", context);

        PreferencesTransport.addTransportation("Transportation_2", 0, "velo", context);
        PreferencesTransport.addTransportation("Transportation_2", 1, "voiture", context);
        PreferencesTransport.addTransportation("Transportation_2", 2, "pied", context);
        PreferencesTransport.addTransportation("Transportation_2", 3, "transport", context);


    }

    @Test
    public void testGetTransportation() {

        assertTrue(PreferencesTransport.getPrefTransportation("Transportation", context).get(0) == "velo");
        assertTrue(PreferencesTransport.getPrefTransportation("Transportation", context).get(1) == "voiture");
        assertTrue(PreferencesTransport.getPrefTransportation("Transportation", context).get(2) == "pied");
        assertTrue(PreferencesTransport.getPrefTransportation("Transportation", context).get(3) == "transport");


    }

    @Test
    public void testRemoveTransportation() {
        PreferencesTransport.removeTransportation("Transportation_2", 0, context);
        assertFalse(PreferencesTransport.getPrefTransportation("Transportation_2", context).get(0) == "velo");
    }

    @Test
    public void testGetOptionTransportation() {
        int[] test = {1, 0, 3, 0};
        PreferencesTransport.setOptionTransportation(fav, context);
        assertTrue(Arrays.equals(test, PreferencesTransport.getOptionTransportation(context)));
        PreferencesTransport.clearOptionTransportation(context);
    }

    @Test
    public void testAddOptionTransportation() {
        int[] test = {1, 2, 0, 0};

        PreferencesTransport.setOptionTransportation(fav, context);
        PreferencesTransport.addOptionTransportation("2", 2, context);
        PreferencesTransport.addOptionTransportation("3", 0, context);

        assertTrue(Arrays.equals(test, PreferencesTransport.getOptionTransportation(context)));
        PreferencesTransport.clearOptionTransportation(context);
    }

    @After
    public void setDown() {
        PreferencesTransport.clearTransportation(context);

    }

}
