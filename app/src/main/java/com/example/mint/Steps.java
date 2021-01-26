package com.example.mint;

public class Steps {
    private String address;
    private int distance;

    public Steps() {
    }

    public Steps(String address, int distance) {
        this.address = address;
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void increaseDistance(int length){
        this.distance += length;
    }
}
