package com.diseasemeter.restful_api.controllers;

import com.diseasemeter.restful_api.resources.heatmap.HeatPoint;
import com.diseasemeter.restful_api.resources.heatmap.HeatPoints;
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
        List<HeatPoint> heatpointsList = Arrays.asList(new HeatPoint[]{});

        if(!name.equals("none")){
            //TODO: select query with name
        }
        if(!date.equals("none")){
            //TODO: select query with date
        }
        return new HeatPoints(heatpointsList);
    }

}
