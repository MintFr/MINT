package com.example.mint.model;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double latitude;
    private double longitude;

    /**
     * Constructor without parameters
     */
    public Coordinates() {
        this.latitude = 0;
        this.longitude = 0;
    }

    /**
     * Constructor using latitude and longitude
     *
     * @param latitude  double
     * @param longitude double
     */
    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    /**
     * Access latitude
     *
     * @return double latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set latitude
     *
     * @param latitude double
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Access longitude
     *
     * @return double longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set longitude
     *
     * @param longitude double
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Test if the coordinates are equal to zero
     *
     * @return boolean
     */
    public boolean isZero() {
        return this.latitude == 0 && this.longitude == 0;
    }
}

