package com.diseasemeter.restful_api.controllers;


import com.diseasemeter.restful_api.bbdd.mysql.DiseaseTransaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.diseasemeter.restful_api.resources.disease.Diseases;

@RestController
public class DiseasesController {

    @RequestMapping(value="/diseases", method = RequestMethod.GET)
    public Diseases diseases(@RequestParam(value="zone", defaultValue="", required = false) String name,
                             @RequestParam(value="date", defaultValue="", required = false) String date) {

        return new Diseases(DiseaseTransaction.listDiseases(name, date));
    }
}
