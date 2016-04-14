package com.diseasemeter.restful_api.resources.heatmap;

import java.util.List;

/**
 * Created by Light on 07/04/16.
 */
public class HeatMapData {

    private List<HeatPoint> heatPointList;
    private List<Center> centerList;

    public HeatMapData(List<HeatPoint> heatPointList, List<Center> centerList) {
        this.heatPointList = heatPointList;
        this.centerList = centerList;
    }

    public List<HeatPoint> getHeatPointList() {
        return heatPointList;
    }

    public List<Center> getCenterList() {
        return centerList;
    }
}
