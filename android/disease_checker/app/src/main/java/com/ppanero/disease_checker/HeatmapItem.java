package com.ppanero.disease_checker;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.List;

/**
 * Created by Light on 13/04/16.
 */
public class HeatmapItem {

    private String name;
    private LatLng markerPoint;
    private List<WeightedLatLng> points;

    public HeatmapItem(String name, LatLng markerPoint, List<WeightedLatLng> points) {
        this.name = name;
        this.markerPoint = markerPoint;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public LatLng getMarkerPoint() {
        return markerPoint;
    }

    public List<WeightedLatLng> getPoints() {
        return points;
    }
}
