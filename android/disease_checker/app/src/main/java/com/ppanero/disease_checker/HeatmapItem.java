package com.ppanero.disease_checker;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.List;
import java.util.Set;

/**
 * Created by Light on 13/04/16.
 */
public class HeatmapItem {

    private Set<MarkerOptions> markers;
    private List<WeightedLatLng> points;

    public HeatmapItem(Set<MarkerOptions> markers, List<WeightedLatLng> points) {
        this.markers = markers;
        this.points = points;
    }

    public Set<MarkerOptions> getMarkers() {
        return markers;
    }

    public List<WeightedLatLng> getPoints() {
        return points;
    }
}
