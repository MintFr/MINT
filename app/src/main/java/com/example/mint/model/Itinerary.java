package com.example.mint.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Itinerary implements Serializable {
    private String timeOption;
    private boolean hourStart;
    private String type; // type of transportation
    private double pollution; // total exposition to pollution
    private double duration; // total itinerary duration
    //private ArrayList<Integer> stepTime; // duration between steps in sec
    private double distance; // total itinerary distance
    //private ArrayList<Integer> stepDistance; // distance between steps in metres
    private ArrayList<double[]> points; // coordinates of each point
    private ArrayList<Step> detail;
    private boolean hasStep;
    private Coordinates step;


    /**
     * Default constructor
     */
    public Itinerary() {
        this.type = "n/a";
        this.pollution = 0.;
        this.duration = 0.;
        this.distance = 0.;
        //this.stepDistance = new ArrayList<>() ;
        //this.stepTime = new ArrayList<>();
        this.points = new ArrayList<>();
    }


    /**
     * Constructor with parameter
     *
     * @param type         String
     * @param pollution    double
     * @param duration     double
     * @param distance     double
     * @param stepTime     ArrayList<Integer>
     * @param stepDistance ArrayList<Integer>
     * @param points       ArrayList<double[]>
     */
    public Itinerary(String type, double pollution, double duration, ArrayList<Integer> stepTime, ArrayList<Integer> stepDistance, ArrayList<double[]> points, double distance) {
        this.type = type;
        this.pollution = pollution;
        this.duration = duration;
        this.distance = distance;
        //this.stepTime=stepTime;
        //this.stepDistance=stepDistance;
        this.points = points;
    }

    /**
     * Constructor from a json object
     *
     * @param json JSONObject
     */
    public Itinerary(JSONObject json) {
        //Reading JSON
        try {
            this.type = json.getString("transport");
            this.distance = json.getDouble("distance");
            this.timeOption = json.getString("time");
            this.hourStart = json.getBoolean("hofStart");
            this.duration = json.getDouble("duration");
            this.pollution = (int) json.getDouble("exposition") / 10.;
            // this.pollution = json.getDouble("exposition");
            this.hasStep = json.getBoolean("hasStep");
            this.points = new ArrayList<>();
            JSONArray tempDetail = json.getJSONArray("details");
            ArrayList<Step> detail = new ArrayList<>();
            for (int i = 0; i < tempDetail.length(); i++) {
                JSONObject step = tempDetail.getJSONObject(i);
                String address = step.getString("addressStep");
                int length = step.getInt("lengthStep");
                int nbEdges = step.getInt("numberOfEdges");
                detail.add(new Step(address, length, nbEdges));
                //System.out.println(address);
            }
            this.setDetail(detail);
            if (hasStep) {
                JSONArray coordStep = json.getJSONArray("step");
                System.out.println(coordStep);
                this.step = new Coordinates(coordStep.getDouble(0), coordStep.getDouble(1));
            }

            JSONArray steps = json.getJSONArray("pointsItinerary");

            for (int i = 0; i < steps.length(); i++) {
                JSONObject point = steps.getJSONObject(i);
                double longitude = point.getDouble("longitude");
                double latitude = point.getDouble("latitude");
                double[] p = {latitude, longitude};
                this.points.add(p);
            }
            int s = this.points.size();
            //this.stepTime = new ArrayList<>();
            //this.stepDistance = new ArrayList<>();
            //JSONArray stepsLength = json.getJSONArray("stepsLength");
            /*for (int j=0;j<stepsLength.length();j++){
                this.stepTime.add(0);
                this.stepDistance.add(stepsLength.getInt(j));
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Copy constructor
     *
     * @param itinerary Itinerary
     */
    public Itinerary(Itinerary itinerary) {
        this.detail = itinerary.getDetail();
        this.hasStep = itinerary.isHasStep();
        this.step = itinerary.getStep();
        this.timeOption = itinerary.getTimeOption();
        this.hourStart = itinerary.isHourStart();
        this.type = itinerary.getType();
        this.pollution = itinerary.getPollution();
        this.duration = itinerary.getDuration();
        this.distance = itinerary.getDistance();
        //this.stepTime = itinerary.getStepTime();
        //this.stepDistance = itinerary.getStepDistance();
        this.points = itinerary.getPoints();
    }

    /**
     * Access itinerary's type of transportation
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Set itinerary's type of transportation
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Access itinerary's score of pollution
     *
     * @return
     */
    public double getPollution() {
        return pollution;
    }

    /**
     * Set itinerary's score of pollution
     *
     * @param pollution
     */
    public void setPollution(Float pollution) {
        this.pollution = pollution;
    }

    /**
     * Set pollution of the itinerary
     *
     * @param pollution
     */
    public void setPollution(double pollution) {
        this.pollution = pollution;
    }

    /**
     * Access total duration
     *
     * @return
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Set total duration
     *
     * @param duration
     */
    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    /*public ArrayList<Integer> getStepTime() {
        return stepTime;
    }

    public void setStepTime(ArrayList<Integer> stepTime) {
        this.stepTime = stepTime;
    }

    public ArrayList<Integer> getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(ArrayList<Integer> stepDistance) {
        this.stepDistance = stepDistance;
    }*/

    /**
     * Access total distance
     *
     * @return
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Set total distance
     *
     * @param distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Access all the successive points of the itinerary
     *
     * @return ArrayList<double [ ]>
     */
    public ArrayList<double[]> getPoints() {
        return points;
    }

    /**
     * Set the list of successive points
     *
     * @param points
     */
    public void setPoints(ArrayList<double[]> points) {
        this.points = points;
    }

    /**
     * Access the total number of Points in the itinerary
     *
     * @return
     */
    public int getPointSize() {
        return points.size();
    }

    /**
     * Access the time Option
     *
     * @return
     */
    public String getTimeOption() {
        return timeOption;
    }

    /**
     * Set the time Option
     *
     * @param timeOption
     */
    public void setTimeOption(String timeOption) {
        this.timeOption = timeOption;
    }

    /**
     * @return
     */
    public boolean isHourStart() {
        return hourStart;
    }

    public void setHourStart(boolean hourStart) {
        this.hourStart = hourStart;
    }

    /**
     * Set total duration
     *
     * @param time
     */
    public void setTime(double time) {
        this.duration = time;
    }

    /**
     * Access detail of the itinerary : all the successive streets to follow
     *
     * @return
     */
    public ArrayList<Step> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<Step> detail) {
        this.detail = detail;
    }

    /**
     * Access the information regarding the existence of a step in the itinerary
     *
     * @return
     */
    public boolean isHasStep() {
        return hasStep;
    }

    public void setHasStep(boolean hasStep) {
        this.hasStep = hasStep;
    }

    public Coordinates getStep() {
        return step;
    }

    public void setStep(Coordinates step) {
        this.step = step;
    }
}
