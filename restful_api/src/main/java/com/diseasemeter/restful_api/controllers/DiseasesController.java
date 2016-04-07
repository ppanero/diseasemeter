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
    public Diseases diseases(@RequestParam(value="zone", defaultValue="none", required = false) String name,
                             @RequestParam(value="date", defaultValue="none", required = false) String date) {

        //TODO: search according to name and date
        Disease d1 = new Disease("Ebola", "Sierra Leona", "27/08/2015", "High", 237834, 20, 3);
        Disease d2 = new Disease("Zika", "Paraguay", "12/03/2016", "High", 137834, 10, 3);
        Disease d3 = new Disease("Dengue", "Argentina", "10/12/2015", "Medium", 7834, 10, 2);
        Disease d4 = new Disease("Malaria", "Brasil", "27/03/2016", "Low", 834, 5, 1);
        /*

        data.add(new DiseaseItem("Ebola", DiseaseLevel.HIGH, "Madrid, 20/10/2016", 34563456, 2, 3464));
        data.add(new DiseaseItem("Zika", DiseaseLevel.HIGH, "Barcelona, 21/10/2016", 3456663, 3, 537));
        data.add(new DiseaseItem("Dengue", DiseaseLevel.MEDIUM, "Vigo, 23/01/2016", 100, 3, 4234));
        data.add(new DiseaseItem("Malaria", DiseaseLevel.MEDIUM, "Malaga, 19/02/2016",1240, 2, 7343));
        data.add(new DiseaseItem("Tifus", DiseaseLevel.MEDIUM, "Madrid, 04/12/2016", 525435, 2, 544));
        data.add(new DiseaseItem("Flew", DiseaseLevel.LOW, "Tarragona, 20/04/2016", 325466, 2, 1111));
        data.add(new DiseaseItem("Hepatitis", DiseaseLevel.LOW, "Bilbao, 09/05/2016", 3534, 1, 5435));
        data.add(new DiseaseItem("Cancer", DiseaseLevel.LOW, "Caceres, 17/03/2016", 53455, 1, 334));
        data.add(new DiseaseItem("AIDS", DiseaseLevel.LOW, "Barcelona, 28/11/2016", 345345, 1, 7443));
         */
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
