package com.diseasemeter.restful_api.resources;

import java.util.List;

/**
 * Created by Light on 07/04/16.
 */
public class HeatPoint {

    private String name;
    private List<Point> pointList; //The first point will have the tag

    public HeatPoint(String name, List<Point> pointList) {
        this.name = name;
        this.pointList = pointList;
    }

    public String getName() {
        return name;
    }

    public List<Point> getPointList() {
        return pointList;
    }
}
