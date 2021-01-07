package com.example.helloworld;

import java.util.ArrayList;

public class Itinerary {
    String type;
    Float pollution;
    Float time;
    ArrayList<double[]> points;

    public Itinerary(String type,Float pollution, Float time, ArrayList<double[]> points) {
        this.type=type;
        this.pollution=pollution;
        this.time=time;
        this.points=points;
    }
}
