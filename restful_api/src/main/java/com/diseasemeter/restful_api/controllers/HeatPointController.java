package com.diseasemeter.restful_api.controllers;

import com.diseasemeter.restful_api.bbdd.mongodb.GeneralOperation;
import com.diseasemeter.restful_api.bbdd.mongodb.MongoComparation;
import com.diseasemeter.restful_api.bbdd.mongodb.MongoDBController;
import com.diseasemeter.restful_api.resources.heatmap.Center;
import com.diseasemeter.restful_api.resources.heatmap.HeatPoint;
import com.diseasemeter.restful_api.resources.heatmap.HeatMapData;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
public class HeatPointController {

    @RequestMapping(value="/heatmap", method = RequestMethod.GET)
    public HeatMapData diseases(@RequestParam(value="disease", defaultValue="none", required = false) String name) {

        List<HeatPoint> heatPointList = null;
        List<Center> centerList = null;

        GeneralOperation<Center> centerOperation = new GeneralOperation<>();
        GeneralOperation<HeatPoint> heatpointOperation = new GeneralOperation<>();

        if(!name.equals("none")){
            Set<Criteria> conditions = new HashSet<>();
            conditions.add(MongoDBController.createCriteria("name", MongoComparation.EQ, name));
            heatPointList = heatpointOperation.filterByConditions(HeatPoint.class, conditions);
            centerList = centerOperation.filterByConditions(Center.class, conditions);
        }
        else{
            heatPointList = heatpointOperation.getAll(HeatPoint.class);
            centerList = centerOperation.getAll(Center.class);
        }
        return new HeatMapData(heatPointList,centerList);
    }

}
