package com.example.mint.model;

import androidx.annotation.ColorInt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class TanMap implements Serializable {
    private String fullName;
    private String type;
    private ArrayList<ArrayList<double[]>> coordinates;
    @ColorInt
    private int color;
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
