package com.diseasemeter.data_colector;


import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;

/**
 * Created by Light on 10/01/16.
 */
public class test {

    public static void main(String[] args){

        GeneralTransaction<Disease> gt = new DiseaseTransaction();
        System.out.println(gt.exists(new Disease("Ebola", "Sierra Leona","10/05/2015", "27/02/2016",3, 237834, 20, 3, true)));
    }
}
