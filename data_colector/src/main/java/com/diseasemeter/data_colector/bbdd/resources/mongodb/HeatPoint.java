package com.diseasemeter.data_colector.bbdd.resources.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Light on 07/04/16.
 */
@Document(collection = "heatpoints")
public class HeatPoint {

    private int weight;
    private long timestamp;
    private String name;
    private String zone;
    private Location location;


    public HeatPoint() {}

    public HeatPoint(int weight, long timestamp, String name, String zone, Location location) {
        this.weight = weight;
        this.timestamp = timestamp;
        this.name = name;
        this.zone = zone;
        this.location = location;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
