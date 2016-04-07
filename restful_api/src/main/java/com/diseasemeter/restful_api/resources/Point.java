package com.diseasemeter.restful_api.resources;


public class Point {

    private double latitude;
    private double longitude;
    private int weight;

    public Point(double latitude, double longitude, int weight) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.weight = weight;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getWeight() {
        return weight;
    }
}
