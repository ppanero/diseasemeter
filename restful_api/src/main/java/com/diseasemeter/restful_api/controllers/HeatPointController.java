package com.diseasemeter.restful_api.controllers;

import com.diseasemeter.restful_api.bbdd.mongodb.MongoDBController;
import com.diseasemeter.restful_api.resources.heatmap.Center;
import com.diseasemeter.restful_api.resources.heatmap.HeatPoint;
import com.diseasemeter.restful_api.resources.heatmap.HeatMapData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class HeatPointController {

    @RequestMapping(value="/heatmap", method = RequestMethod.GET)
    public HeatMapData diseases(@RequestParam(value="disease", defaultValue="none", required = false) String name) {

        List<HeatPoint> heatPointList = null;
        List<Center> centerList = null;

        if(!name.equals("none")){

            heatPointList = MongoDBController.filterHeatPointsByName(name);
            centerList = MongoDBController.filterCentersByName(name);
        }
        else{
            heatPointList = MongoDBController.getAllHeatpoints();
            centerList = MongoDBController.getAllCenters();
        }
        return new HeatMapData(heatPointList,centerList);
    }

}
