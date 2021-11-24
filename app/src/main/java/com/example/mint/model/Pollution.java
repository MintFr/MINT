package com.example.mint.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Pollution {
    private Coordinates start;
    private Coordinates end;
    private double pol;

    public Pollution(JSONObject json) throws JSONException {
        this.start = new Coordinates(json.getDouble("y1"), json.getDouble("x1"));
        this.end = new Coordinates(json.getDouble("y2"), json.getDouble("x2"));
        this.pol = json.getDouble("conc_no2");
    }

    public Coordinates getStart() {
        return start;
    }

    public void setStart(Coordinates start) {
        this.start = start;
    }

    public Coordinates getEnd() {
        return end;
    }

    public void setEnd(Coordinates end) {
        this.end = end;
    }

    public double getPol() {
        return pol;
    }

    public void setPol(double pol) {
        this.pol = pol;
    }
}
