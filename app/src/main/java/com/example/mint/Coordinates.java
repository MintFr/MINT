package com.example.mint;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double latitude;
    private double longitude;

    public Coordinates() {
        this.latitude = 0;
        this.longitude = 0;
    }

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isZero(){
        return this.latitude == 0 && this.longitude == 0;
    }
}
