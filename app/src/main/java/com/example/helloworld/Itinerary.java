package com.example.helloworld;

import java.util.ArrayList;

public class Itinerary {
    String type;
    double pollution;
    double time;
    ArrayList<double[]> points;

    public Itinerary(String type,double pollution, double time, ArrayList<double[]> points) {
        this.type=type;
        this.pollution=pollution;
        this.time=time;
        this.points=points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPollution() {
        return pollution;
    }

    public void setPollution(Float pollution) {
        this.pollution = pollution;
    }

    public double getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public ArrayList<double[]> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<double[]> points) {
        this.points = points;
    }

    public int getPointSize() {
        return points.size();
    }
}
