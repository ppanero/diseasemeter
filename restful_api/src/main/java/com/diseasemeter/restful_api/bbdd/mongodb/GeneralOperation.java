package com.diseasemeter.restful_api.bbdd.mongodb;


import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by Light on 19/04/16.
 */
public class GeneralOperation<K> {

    public boolean exists(Set<Criteria> conditions, Class<K> kClass){
        Query filterQuery = new Query();
        for(Criteria condition : conditions){
            filterQuery.addCriteria(condition);
        }
        return MongoDBController.getMongoOperations().exists(filterQuery, kClass);
    }

    public void insert(K data){
        MongoDBController.getMongoOperations().save(data);
    }

    public List<K> getAll(Class<K> kClass){
        return MongoDBController.getMongoOperations().findAll(kClass);
    }

    public List<K> filterByConditions(Class<K> kClass, Set<Criteria> conditions){
        Query filterQuery = new Query();
        for(Criteria condition : conditions){
            filterQuery.addCriteria(condition);
        }
        return MongoDBController.getMongoOperations().find(filterQuery, kClass);
    }

    public boolean existsAtMaxDistance(Class<K> kClass, String geo2dattr, Double[] coordinates, int distance,
                                       Set<Criteria> conditions){
        Query filterQuery = new Query();
        filterQuery.addCriteria(Criteria.where(geo2dattr).nearSphere(new GeoJsonPoint(
                new Point(coordinates[0], coordinates[1]))).maxDistance(distance));
        for(Criteria condition : conditions){
            filterQuery.addCriteria(condition);
        }
        return !MongoDBController.getMongoOperations().find(filterQuery, kClass).isEmpty();
    }
}
