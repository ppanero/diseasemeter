package com.diseasemeter.restful_api.resources.heatmap;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * Created by Light on 14/04/16.
 */
@Document(collection = "centers")
public class Center {

    private String name;
    private String zone;
    private long timestamp;
    private Set<Location> points;


    public Center() {}

    public Center(String name, String zone, long timestamp, Set<Location> points) {
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Set<Location> getPoints() {
        return points;
    }

    public void setPoints(Set<Location> points) {
        this.points = points;
    }
}
