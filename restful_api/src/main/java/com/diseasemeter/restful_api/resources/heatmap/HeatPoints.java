package com.diseasemeter.restful_api.resources.heatmap;

import java.util.List;

/**
 * Created by Light on 07/04/16.
 */
public class HeatPoints {

    private List<HeatPoint> heatPointList;


    public HeatPoints(List<HeatPoint> heatPointList) {
        this.heatPointList = heatPointList;
    }

    public List<HeatPoint> getHeatPointList() {
        return heatPointList;
    }
}
