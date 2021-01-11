package com.example.helloworld;

import android.location.Geocoder;

import java.util.Locale;

import static java.util.Locale.getDefault;

public class Address {
    private String locationName;
    private Coordinates coordinates;

    public Address() {
    }

    public Address(String locationName, Coordinates coordinates) {
        this.locationName = locationName;
        this.coordinates = coordinates;
    }

    public Address(String locationName) {
        this.locationName = locationName;
    }

    public Address(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void coordinatesToLocationName(){
        
    }

    public void locationNameToCoordinates(){

    }
}
