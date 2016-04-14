package com.diseasemeter.restful_api.bbdd.mongodb;

import com.diseasemeter.restful_api.resources.heatmap.Center;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Light on 14/04/16.
 */
public interface CenterRepository extends MongoRepository<Center, String> {

}
