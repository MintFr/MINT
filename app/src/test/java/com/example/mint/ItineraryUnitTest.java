package com.example.mint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.mint.model.Itinerary;
import com.example.mint.model.Step;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ItineraryUnitTest {


    /**
     * This tests only the basic attributes of itinerary
     *
     * @throws JSONException
     */
    @Test
    public void jsonConstructorTest1() {

        // Reading file from resources and casting it in JSONObject

        String resourceName = "src/test/java/resources/jsonTest.json";
        File file = new File(resourceName);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonObj = new JSONObject(content);

            Itinerary output = new Itinerary(jsonObj);

            assertEquals(45.3, output.getDistance(), 0.001);
            assertEquals(150.3, output.getDuration(), 0.001);
            assertEquals(150.0, output.getPollution(), 0.0001);
            assertEquals("\"voiture\"", output.getType());
            assertEquals("\"15:30\"", output.getTimeOption());
            assertTrue(output.isHourStart());
            assertFalse(output.isHasStep());
            assertNull(output.getStep());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void constructorJSONDetails() {

        // Reading file from resources and casting it in JSONObject

        String resourceName = "src/test/java/resources/jsonTest.json";
        File file = new File(resourceName);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonObj = new JSONObject(content);

            Itinerary output = new Itinerary(jsonObj);

            ArrayList<Step> expectedDetails = new ArrayList<>();
            expectedDetails.add(
                    new Step(
                            "adresse 1",
                            15
                    )
            );
            expectedDetails.add(
                    new Step(
                            "adresse   2",
                            30
                    )
            );

            assertEquals(expectedDetails.size(), output.getDetail().size());
            for (int i = 0; i < output.getDetail().size(); i++) {
                assertEquals(
                        expectedDetails.get(i).getAddress(),
                        output.getDetail().get(i).getAddress()
                );
                assertEquals(
                        expectedDetails.get(i).getDistance(),
                        output.getDetail().get(i).getDistance()
                );
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void constructorJSONPointsItinerary() {
        // Reading file from resources and casting it in JSONObject
        String resourceName = "src/test/java/resources/jsonTest.json";
        File file = new File(resourceName);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonObj = new JSONObject(content);

            Itinerary output = new Itinerary(jsonObj);

            ArrayList<double[]> outputPoints = output.getPoints();

            ArrayList<double[]> expectedPoints = new ArrayList<>();
            expectedPoints.add(new double[]{15.3, 30});
            expectedPoints.add(new double[]{150.03, 300.15});

            assertEquals(outputPoints.size(), expectedPoints.size());
            assertEquals(outputPoints.getClass(), expectedPoints.getClass());
            for (int i = 0; i < outputPoints.size(); i++) {
                assertEquals(expectedPoints.get(i).length, outputPoints.get(i).length);
                assertEquals(expectedPoints.get(i)[0], outputPoints.get(i)[0], 0.001);
                assertEquals(expectedPoints.get(i)[1], outputPoints.get(i)[1], 0.001);
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void constructorJSONStepItinerary() {
        // Reading file from resources and casting it in JSONObject
        String resourceName = "src/test/java/resources/jsonStepTest.json";
        File file = new File(resourceName);

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonObj = new JSONObject(content);

            Itinerary output = new Itinerary(jsonObj);
            assertTrue(output.isHasStep());

            double stepLat = 47.2471341;
            double stepLong = -1.5525426;
            boolean isStepReached = false;
            double epsilon = 0.0001;
            for (int i = 0; i < output.getPointSize(); i++) {
                double[] coordi = output.getPoints().get(i);
                if (
                        (Math.abs(stepLat - coordi[0]) < epsilon)
                                && (Math.abs(stepLong - coordi[1]) < epsilon)
                ) {
                    isStepReached = true;
                    break;
                }
            }
            assertTrue(isStepReached);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}