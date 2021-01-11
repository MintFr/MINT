package com.example.helloworld;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Itinerary implements Serializable {
    private String type; // type of transportation
    private double pollution; // total exposition to pollution
    private double time; // total itinerary time
    private ArrayList<int[]> stepTime; // time between steps in sec
    private ArrayList<int[]> stepDistance; // distance between steps in metres
    private ArrayList<double[]> points; // coordinates of each point


    /**
     * Default constructor
     */
    public Itinerary(){
        this.type = "n/a";
        this.pollution = 0.;
        this.time = 0.;
        this.stepDistance = new ArrayList<>() ;
        this.stepTime = new ArrayList<>();
        this.points = new ArrayList<>();
    }


    /**
     * Constructor with parameter
     * @param type
     * @param pollution
     * @param time
     * @param stepTime
     * @param stepDistance
     * @param points
     */
    public Itinerary(String type,double pollution, double time, ArrayList<int[]> stepTime, ArrayList<int[]> stepDistance, ArrayList<double[]> points) {
        this.type=type;
        this.pollution=pollution;
        this.time=time;
        this.stepTime=stepTime;
        this.stepDistance=stepDistance;
        this.points=points;
    }

    /**
     * Constructor from a json object
     * @param json
     */
    public Itinerary(JSONObject json){
        //Reading JSON
        try{

            this.type = json.getString("type");
            this.pollution = json.getDouble("exposition");
            this.time = json.getDouble("time");
            this.stepTime = (ArrayList<int[]>) json.get("stepTime");
            this.stepDistance = (ArrayList<int[]>) json.get("stepDistance");
            JSONArray c = json.getJSONArray("steps");

            for (int i = 0; i<c.length(); i++)
            {
                JSONObject point = c.getJSONObject(i);
                double longitude = point.getDouble("longitude");
                double latitude = point.getDouble("latitude");
                double[] p = {latitude,longitude};
                this.points.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * Copy constructor
     * @param itinerary
     */
    public Itinerary(Itinerary itinerary){
        this.type = itinerary.getType();
        this.pollution = itinerary.getPollution();
        this.time = itinerary.getTime();
        this.stepTime = itinerary.getStepTime();
        this.stepDistance = itinerary.getStepDistance();
        this.points = itinerary.getPoints();
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
