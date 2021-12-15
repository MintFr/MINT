package com.example.mint;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.mint.model.Address;
import com.example.mint.model.Coordinates;

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
        Coordinates coordinates = new Coordinates(1.5,1.5);
        address.setCoordinates(coordinates);
        double[] expected = new double[] {1.5,1.5};
        assertTrue(address.getCoordinates().getLongitude()==1.5);
        assertTrue(address.getCoordinates().getLatitude()==1.5);
    }

    @Test
    public void setCoordinatesDouble(){
        Address address = new Address();
        address.setCoordinates(1.5,1.5);
        double[] expected = new double[] {1.5,1.5};
        assertTrue(address.getCoordinates().getLongitude()==1.5);
        assertTrue(address.getCoordinates().getLatitude()==1.5);
    }

}