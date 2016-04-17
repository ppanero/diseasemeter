package com.diseasemeter.data_colector.bbdd.resources.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Created by Light on 14/04/16.
 */
@Document(collection = "centers")
public class Center {

    private String name;
    private String zone;
    private long timestamp;
    private Location location;

    public Center() { }

    public Center(String name, String zone, long timestamp, Location location) {
        this.name = name;
        this.zone = zone;
        this.timestamp = timestamp;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
