package com.diseasemeter.data_colector;


import com.diseasemeter.data_colector.bbdd.mongodb.GeneralOperation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoComparation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoDBController;
import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Center;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.HeatPoint;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Location;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.bbdd.resources.mysql.DiseaseKey;
import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.google_api.Geocoder;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){

        GeneralOperation<HeatPoint> heatpointOperation = new GeneralOperation<HeatPoint>();

        Double[] coordinates = new Double[]{-85.60236429999999,12.7690126};

        Set<Criteria> conditions = new HashSet<Criteria>();
        conditions.add(MongoDBController.createCriteria("name", MongoComparation.EQ, "Chikungunya"));

        if(!heatpointOperation.exists(conditions, HeatPoint.class))
            System.out.printf("correct");

    }
}