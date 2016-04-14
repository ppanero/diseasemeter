package com.diseasemeter.restful_api.resources.heatmap;

import java.util.Set;

/**
 * Created by Light on 14/04/16.
 */
public class Center {

    private String name;
    private String zone;
    private int timestamp;
    private Set<Location> points;


    public Center() {}

    public Center(String name, String zone, int timestamp, Set<Location> points) {
        this.name = name;
        this.zone = zone;
        this.timestamp = timestamp;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Set<Location> getPoints() {
        return points;
    }

    public void setPoints(Set<Location> points) {
        this.points = points;
    }
}
