package com.example.mint;
import static org.junit.Assert.assertTrue;

import com.example.mint.model.Coordinates;

import org.junit.Test;



public class CoordinatesTest {
    @Test
    public void setCoordinatesIsCorrect() {
        Coordinates coordinates = new Coordinates();
        coordinates.setLongitude(1.5);
        coordinates.setLatitude(2.5);
        assertTrue(coordinates.getLongitude() == 1.5);
        assertTrue(coordinates.getLatitude() == 2.5);
    }

    @Test
    public void isZeroIsCorrect(){
        Coordinates coordinates = new Coordinates();
        assertTrue(coordinates.isZero());
    }

}
