package com.diseasemeter.data_colector.bbdd.mongodb;

import com.diseasemeter.data_colector.bbdd.resources.mongodb.Center;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.HeatPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.geo.Point;
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

    public static boolean existsCenter(Center center, int distance){
        double[] coordinates = center.getLocation().getCoordinates();
        Query filterCenters = new Query(Criteria.where("location").nearSphere(new Point(coordinates[0], coordinates[1])).maxDistance(distance));

        return !mongoOperation.find(filterCenters, Center.class).isEmpty();
    }

    public static List<HeatPoint> filterHeatPointsByName(String name) {
        Query filterByNameQuery = new Query(Criteria.where("name").is(name));
        return mongoOperation.find(filterByNameQuery,HeatPoint.class);
    }

    public static List<Center> filterCentersByName(String name){
        Query filterByNameQuery = new Query(Criteria.where("name").is(name));
        return mongoOperation.find(filterByNameQuery, Center.class);
    }

    public static void insertCenter(Center c){
        mongoOperation.save(c);
    }

    public static void inserteHeatpoint(HeatPoint hp){
        mongoOperation.save(hp);
    }

    public static List<HeatPoint> getAllHeatpoints() {
        return mongoOperation.findAll(HeatPoint.class);
    }

    public static List<Center> getAllCenters(){
        return mongoOperation.findAll(Center.class);
    }
}
