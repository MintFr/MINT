package com.example.mint;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

import android.renderscript.ScriptGroup;

import com.example.mint.model.Itinerary;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;


public class ItineraryUnitTest {
    @Test
    public void itineraryIsCorrect() {

        File file = null;
        URL url = this.getClass().getClassLoader().getResource("jsonTest.json");

        //System.out.println(url.getPath());
        try {
            file = Paths.get(url.toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assertNotNull(this.getClass().getClassLoader().getResource("jsonTest.json"));

        String result = "{/D:/Dev/Mint/Fullcode/MINT/app/build/intermediates/javac/debugUnitTest/classes/jsonTest.json}";
        try {
            System.out.println("on entre dans le try");
            JSONObject jObject = new JSONObject(result);
            System.out.println("on crée je JSONObject");
            Itinerary itinerary = new Itinerary(jObject);
            assertEquals(itinerary.getDistance(),15.3,0.001);
            System.out.println("on passe dans le try");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}


