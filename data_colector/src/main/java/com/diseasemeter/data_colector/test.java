package com.diseasemeter.data_colector;


import com.diseasemeter.data_colector.bbdd.mongodb.MongoDBController;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Center;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Location;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){

        MongoDBController.insertCenter(new Center("test", "test", 12312312, new Location(new double[]{41.274941, -4.034504})));
    }
}