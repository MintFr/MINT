package com.example.mint;

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
    public Itinerary(){
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
     * @param type String
     * @param pollution double
     * @param duration double
     * @param distance double
     * @param stepTime ArrayList<Integer>
     * @param stepDistance ArrayList<Integer>
     * @param points ArrayList<double[]>
     */
    public Itinerary(String type, double pollution, double duration, ArrayList<Integer> stepTime, ArrayList<Integer> stepDistance, ArrayList<double[]> points, double distance) {
        this.type=type;
        this.pollution=pollution;
        this.duration = duration;
        this.distance=distance;
        //this.stepTime=stepTime;
        //this.stepDistance=stepDistance;
        this.points=points;
    }

    /**
     * Constructor from a json object
     * @param json JSONObject
     */
    public Itinerary(JSONObject json){
        //Reading JSON
        try{
            this.type = json.getString("transport");
            this.distance = json.getDouble("distance");
            this.timeOption = json.getString("time");
            this.hourStart = json.getBoolean("hofStart");
            this.duration = json.getDouble("duration");
            this.pollution = (int) json.getDouble("exposition");
            this.hasStep = json.getBoolean("hasStep");
            this.points = new ArrayList<>();
            JSONArray tempDetail = json.getJSONArray("details");
            ArrayList<Step> detail = new ArrayList<>();
            for(int i = 0; i<tempDetail.length(); i++){
                JSONObject step = tempDetail.getJSONObject(i);
                String address = step.getString("addressStep");
                int length = step.getInt("lengthStep");
                detail.add(new Step(address, length));
                //System.out.println(address);
            }
            this.setDetail(detail);
            if (hasStep){
            JSONArray coordStep = json.getJSONArray("step");
            System.out.println(coordStep);
            this.step = new Coordinates(coordStep.getDouble(0), coordStep.getDouble(1));
            }

            JSONArray steps = json.getJSONArray("pointsItinerary");

            for (int i = 0; i<steps.length(); i++)
            {
                JSONObject point = steps.getJSONObject(i);
                double longitude = point.getDouble("longitude");
                double latitude = point.getDouble("latitude");
                double[] p = {latitude,longitude};
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
     * @param itinerary Itinerary
     */
    public Itinerary(Itinerary itinerary){
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public double getDistance(){
        return distance;
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

    public ArrayList<double[]> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<double[]> points) {
        this.points = points;
    }

    public int getPointSize() {
        return points.size();
    }

    public String getTimeOption() {
        return timeOption;
    }

    public void setTimeOption(String timeOption) {
        this.timeOption = timeOption;
    }

    public boolean isHourStart() {
        return hourStart;
    }

    public void setHourStart(boolean hourStart) {
        this.hourStart = hourStart;
    }

    public void setPollution(double pollution) {
        this.pollution = pollution;
    }

    public void setTime(double time) {
        this.duration = time;
    }

    public ArrayList<Step> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<Step> detail) {
        this.detail = detail;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

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
