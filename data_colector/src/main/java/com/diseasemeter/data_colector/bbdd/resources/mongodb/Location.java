package com.diseasemeter.data_colector.bbdd.resources.mongodb;

/**
 * coordinates[2]
 * [0] is lng
 * [1] is lat
 */
public class Location {

    private final String type = "Point";
    private Double[] coordinates;

    public Location() {}

    public Location(Double[] coordinates) {
        this.coordinates = coordinates;
    }

    public  String getType() {
        return type;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}
