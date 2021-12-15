package com.example.mint;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.mint.model.Coordinates;



public class CoordinatesTest {
    @Test
    public void setCoordinatesIsCorrect() {
        Coordinates coordinates = new Coordinates();
        coordinates.setLongitude(1.5);
        coordinates.setLatitude(1.5);
        assertTrue(coordinates.getLongitude()==1.5);
        assertTrue(coordinates.getLatitude()==1.5);
    }

    @Test
    public void isZeroIsCorrect(){
        Coordinates coordinates = new Coordinates();
        assertTrue(coordinates.isZero());
    }

}
