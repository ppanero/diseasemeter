package com.diseasemeter.restful_api.controllers;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.diseasemeter.restful_api.resources.Disease;
import com.diseasemeter.restful_api.resources.Diseases;

@RestController
public class DiseasesController {

    @RequestMapping(value="/diseases", method = RequestMethod.GET)
    public Diseases diseases(@RequestParam(value="name", defaultValue="none", required = false) String name,
                             @RequestParam(value="date", defaultValue="none", required = false) String date) {

        //TODO: search according to name and date
        Disease d1 = new Disease("Ebola", "Sierra Leona", "27/08/2015", "High", 237834, 20, 3);
        Disease d2 = new Disease("Zika", "Paraguay", "12/03/2016", "High", 137834, 10, 3);
        Disease d3 = new Disease("Dengue", "Argentina", "10/12/2015", "Medium", 7834, 10, 2);
        Disease d4 = new Disease("Malaria", "Brasil", "27/03/2016", "Low", 834, 5, 1);
        List<Disease> diseaseList = Arrays.asList(new Disease[]{d1,d2,d3,d4});

        if(!name.equals("none")){
            //TODO: select query with name
        }
        if(!date.equals("none")){
            //TODO: select query with date
        }
        return new Diseases(diseaseList);
    }
}
