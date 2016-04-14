package com.diseasemeter.restful_api.resources.heatmap;

/**
 * Created by Light on 14/04/16.
 */
public class Location {

    private static final String type = "Point";
    private double[] coordinates;

    public Location() {}

    public Location(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public static String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
