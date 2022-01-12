package com.example.mint;


import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.mint.model.PreferencesSensibility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstrumentTestPrefencesSensibility {
    Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void setup() {
        PreferencesSensibility.setSensibility("Sensibility", "Élevée", context);

    }

    @Test
    public void testGetAddress() {
        assertTrue(PreferencesSensibility.getSensibility("Sensibility", context) == "Élevée");


    }

    @After
    public void setDown() {
        PreferencesSensibility.removeSensibility("Sensibility", context);
    }

}
