package com.example.helloworld;

import java.util.ArrayList;

public class Itinerary {
    public String type; // type of transportation
    public double pollution; // total exposition to pollution
    public double time; // total itinerary time
    public ArrayList<int[]> stepTime; // time between steps in sec
    public ArrayList<int[]> stepDistance; // distance between steps in metres
    public ArrayList<double[]> points; // coordinates of each point

    public Itinerary(String type,double pollution, double time, ArrayList<int[]> stepTime, ArrayList<int[]> stepDistance, ArrayList<double[]> points) {
        this.type=type;
        this.pollution=pollution;
        this.time=time;
        this.stepTime=stepTime;
        this.stepDistance=stepDistance;
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

    public ArrayList<int[]> getStepTime() {
        return stepTime;
    }

    public void setStepTime(ArrayList<int[]> stepTime) {
        this.stepTime = stepTime;
    }

    public ArrayList<int[]> getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(ArrayList<int[]> stepDistance) {
        this.stepDistance = stepDistance;
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
