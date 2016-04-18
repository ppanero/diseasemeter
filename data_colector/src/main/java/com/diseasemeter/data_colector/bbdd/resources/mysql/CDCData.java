package com.diseasemeter.data_colector.bbdd.resources.mysql;

import com.diseasemeter.data_colector.common.MACRO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Light on 17/04/16.
 */
@Entity
@Table(name = "cdc_data")
public class CDCData extends GeneralResource implements Serializable {

    @Id
    @Column(name = "_disease")
    private String name;
    @Id
    @Column(name = "_location")
    private String location;
    @Id
    @Column(name = "_date")
    private String date;
    @Column(name = "disease_extra")
    private String diseaseExtra;
    @Column(name = "location_extra")
    private String locationExtra;
    @Column(name = "level")
    private int level;
    @Column(name = "weight")
    private int weight;

    public CDCData() { }

    public CDCData(String name, String location, String date, String diseaseExtra, String locationExtra, int level, int weight) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.diseaseExtra = diseaseExtra;
        this.locationExtra = locationExtra;
        this.level = level;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiseaseExtra() {
        return diseaseExtra;
    }

    public void setDiseaseExtra(String diseaseExtra) {
        this.diseaseExtra = diseaseExtra;
    }

    public String getLocationExtra() {
        return locationExtra;
    }

    public void setLocationExtra(String locationExtra) {
        this.locationExtra = locationExtra;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public Object[] getKeyValues() {
        return new Object[]{this.name, this.location, this.date};
    }

    public static int getWeightFromLevel(int level){
        if(level == 1) return 100;
        else if (level == 2) return 250;
        else if (level == 3) return 500;
        else return 0;
    }

    @Override
    public String toString(){
        String dExtra = this.diseaseExtra;
        if(dExtra == null) dExtra = " null disease extra";
        String pExtra = this.locationExtra;
        if(pExtra == null) pExtra = " null place extra";
        return this.name.concat(MACRO.COMMA).concat(dExtra).concat(MACRO.COMMA).concat(this.date).concat(MACRO.COMMA)
                .concat(this.location).concat(MACRO.COMMA).concat(pExtra).concat(MACRO.COMMA)
                .concat(String.valueOf(this.level)).concat(MACRO.COMMA).concat(String.valueOf(this.weight));
    }
}
