package com.diseasemeter.data_colector;


import com.diseasemeter.data_colector.monkey_learn.Processor;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){
        Processor.Processor();
        Processor.locationExtractor(2, new String[]{"Hubo una mierda grande en Japon otra en MAdrid y pum otra en aviles, tambien se vio afectado cartagena"});
    }
}