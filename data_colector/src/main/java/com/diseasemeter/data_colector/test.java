package com.diseasemeter.data_colector;


import com.diseasemeter.data_colector.google_api.Geocoder;
import com.diseasemeter.data_colector.monkey_learn.Processor;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){
        //new Processor().sentimentAnalysis(2, new String[]{"Hace buen dia","Esto es malo malo y debería ser un -1",
        //                               "Soy feliz esto es 1"});

        Double[] d = Geocoder.geocode("Barcelona");

        System.out.println(d[0]);
        System.out.println(d[1]);
    }
}
