package com.diseasemeter.restful_api.bbdd.mongodb;

import com.diseasemeter.restful_api.resources.heatmap.HeatPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Light on 14/04/16.
 */
public interface HeatpointRepository extends MongoRepository<HeatPoint, String> {

}
