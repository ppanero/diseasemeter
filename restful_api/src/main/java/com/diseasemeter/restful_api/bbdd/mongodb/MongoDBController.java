package com.diseasemeter.restful_api.bbdd.mongodb;

import com.diseasemeter.restful_api.resources.heatmap.Center;
import com.diseasemeter.restful_api.resources.heatmap.HeatPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Created by Light on 14/04/16.
 */

public class MongoDBController {


    private static final ApplicationContext ctx = new GenericXmlApplicationContext("SpringConfig.xml");
    private static MongoOperations mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");

    public static List<HeatPoint> filterHeatPointsByName(String name) {
        Query filterByNameQuery = new Query(Criteria.where("name").is(name));
        return mongoOperation.find(filterByNameQuery,HeatPoint.class);
    }

    public static List<Center> filterCentersByName(String name){
        Query filterByNameQuery = new Query(Criteria.where("name").is(name));
        return mongoOperation.find(filterByNameQuery, Center.class);
    }

    public static List<HeatPoint> getAllHeatpoints() {
        return mongoOperation.findAll(HeatPoint.class);
    }

    public static List<Center> getAllCenters(){
        return mongoOperation.findAll(Center.class);
    }
}
