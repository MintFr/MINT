package com.example.mint;

import static org.junit.Assert.assertTrue;

import com.example.mint.model.Address;
import com.example.mint.model.Coordinates;

import org.junit.Test;

public class AddressUnitTest {
    @Test
    public void setLocationsIsCorrect() {
        Address address = new Address();
        address.setLocationName("Hello");
        assertTrue(address.getLocationName().equals("Hello"));
    }

    @Test
    public void setCoordinatesIsCorrect() {
        Address address = new Address();
        Coordinates coordinates = new Coordinates(15.0,1.5);
        address.setCoordinates(coordinates);
        double[] expected = new double[] {15.0,1.5}; // {lat, long}
        assertTrue(address.getCoordinates().getLongitude() == expected[1]);
        assertTrue(address.getCoordinates().getLatitude() == expected[0]);
    }

    @Test
    public void setCoordinatesDouble(){
        Address address = new Address();
        address.setCoordinates(1.5,1.5);
        double[] expected = new double[] {1.5,1.5};
        assertTrue(address.getCoordinates().getLongitude() == expected[1]);
        assertTrue(address.getCoordinates().getLatitude() == expected[0]);
    }

}