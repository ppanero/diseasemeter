package com.diseasemeter.restful_api.resources.disease;

import com.diseasemeter.restful_api.resources.disease.Disease;

import java.util.List;

/**
 * Created by Light on 07/04/16.
 */
public class Diseases {

    List<Disease> diseases;

    public Diseases(List<Disease> diseases) {
        this.diseases = diseases;
    }

    public List<Disease> getDiseases() {
        return diseases;
    }
}
