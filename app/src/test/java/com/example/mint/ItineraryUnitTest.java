package com.example.mint;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.mint.model.Itinerary;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;


public class ItineraryUnitTest {
    @Test
    public void itineraryIsCorrect() {

        File file = null;
        URL url = this.getClass().getClassLoader().getResource("jsonTest.json");

        System.out.println(url.getPath());
        try {
            file = Paths.get(url.toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assertNotNull(this.getClass().getClassLoader().getResource("jsonTest.json"));

        Itinerary itinerary = new Itinerary();
    }
}
