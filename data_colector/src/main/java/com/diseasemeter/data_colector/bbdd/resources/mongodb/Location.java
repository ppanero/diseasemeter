package com.diseasemeter.data_colector.bbdd.resources.mongodb;

/**
 * Created by Light on 14/04/16.
 */
public class Location {

    private final String type = "Point";
    private double[] coordinates;

    public Location() {}

    public Location(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public  String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
