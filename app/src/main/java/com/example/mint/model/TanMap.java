package com.example.mint.model;

import androidx.annotation.ColorInt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * One line of public transportation, used in array to display the whole map from the JSON that represents all the lines
 */
public class TanMap implements Serializable {
    /**
     * Name of both extremities of the line
     */
    private String fullName;
    /**
     * Type of transportation (bus/tram)
     */
    private String type;
    /**
     * Both list of the couple of coordinates of the stops in each ways, we have 2 list of coordinates because some lines do not have the same path back and forth
     */
    private ArrayList<ArrayList<double[]>> coordinates;
    @ColorInt
    private int color;
    /**
     * Number of the line ex: 26, C20
     */
    private String shortName;

    public TanMap() {

    }

    public TanMap(String fullName, String type, ArrayList<ArrayList<double[]>> coordinates, int color, String shortName) {
        this.fullName = fullName;
        this.type = type;
        this.coordinates = coordinates;
        this.color = color;
        this.shortName = shortName;
    }

    /**
     * Creates a new line of public transportation from a JSON
     *
     * @param json the line we want to create
     * @throws JSONException
     */
    public TanMap(JSONObject json) throws JSONException {
        this.fullName = json.getString("fullName");
        this.type = json.getString("type");
        this.coordinates = new ArrayList<>();
        JSONArray tempo = json.getJSONArray("coordinates");
        for (int j = 0; j < tempo.length(); j++) {
            ArrayList<double[]> tempo2 = new ArrayList<double[]>();
            for (int k = 0; k < tempo.getJSONArray(j).length(); k++) {
                double[] tempo3 = new double[2];
                for (int l = 0; l < tempo.getJSONArray(j).getJSONArray(k).length(); l++) {
                    tempo3[l] = tempo.getJSONArray(j).getJSONArray(k).getDouble(l);
                }
                tempo2.add(tempo3);
            }
            this.coordinates.add(tempo2);
        }

        this.color = Integer.decode(String.format("0x%s", json.getString("color")));
        this.shortName = json.getString("shortName");
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<ArrayList<double[]>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<double[]>> coordinates) {
        this.coordinates = coordinates;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
