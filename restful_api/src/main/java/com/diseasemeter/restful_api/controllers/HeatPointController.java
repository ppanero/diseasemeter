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

    @RequestMapping(value="/heatmap", method = RequestMethod.GET)
    public HeatPoints diseases(@RequestParam(value="name", defaultValue="none", required = false) String name,
                               @RequestParam(value="date", defaultValue="none", required = false) String date) {

        //TODO: search according to name and date

        Point p1 = new Point(40.416872, -3.703717, 1000);
        Point p2= new Point(40.543272, -3.767217, 800);
        Point p3 = new Point(40.096872, -3.712717, 600);
        Point p4 = new Point(40.41572, -3.343457, 50);
        Point p5 = new Point(40.435372, -3.703717, 100);
        Point p6 = new Point(40.412332, -3.734517, 600);
        Point p7 = new Point(40.422734, -3.5065717, 1000);
        Point p8 = new Point(40.478872, -3.704617, 60);
        Point p9 = new Point(40.830072, -3.723417, 100);
        Point p10 = new Point(40.326872, -3.703717, 90);
        Point p11 = new Point(40.234572, -3.603497, 10);


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
