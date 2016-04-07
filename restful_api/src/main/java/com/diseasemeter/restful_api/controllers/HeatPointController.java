package com.diseasemeter.restful_api.controllers;

import com.diseasemeter.restful_api.resources.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
public class HeatPointController {

    @RequestMapping(value="/heatpoint", method = RequestMethod.GET)
    public HeatPoints diseases(@RequestParam(value="name", defaultValue="none", required = false) String name,
                               @RequestParam(value="date", defaultValue="none", required = false) String date) {

        //TODO: search according to name and date

        Point p1 = new Point(41.394640, 2.172455, 1000);
        Point p2= new Point(41.372307, 2.125695, 800);
        Point p3 = new Point(41.449830, 2.225583, 600);
        Point p4 = new Point(41.500640, 2.175555, 50);
        Point p5 = new Point(41.444640, 2.022455, 100);
        Point p6 = new Point(41.447440, 2.042455, 600);
        Point p7 = new Point(41.398230, 2.172365, 1000);
        Point p8 = new Point(41.379240, 2.042455, 60);
        Point p9 = new Point(41.408680, 2.032595, 100);
        Point p10 = new Point(41.373340, 2.012455, 90);
        Point p11 = new Point(41.401230, 2.135875, 10);
        List<Point> pointList = Arrays.asList(new Point[]{p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11});
        HeatPoint hp1 = new HeatPoint("Ebola", pointList);
        List<HeatPoint> heatpointsList = Arrays.asList(new HeatPoint[]{hp1});

        if(!name.equals("none")){
            //TODO: select query with name
        }
        if(!date.equals("none")){
            //TODO: select query with date
        }
        return new HeatPoints(heatpointsList);
    }

}
