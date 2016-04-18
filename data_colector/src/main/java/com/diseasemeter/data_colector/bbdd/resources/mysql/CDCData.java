package com.diseasemeter.data_colector.bbdd.resources.mysql;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Light on 17/04/16.
 */
@Entity
@Table(name = "cdc_data")
public class CDCData extends GeneralResource<CDCDataKey> implements Serializable {

    @EmbeddedId
    private CDCDataKey cdcDataKey;
    @Column(name = "disease_extra")
    private String diseaseExtra;
    @Column(name = "location_extra")
    private String locationExtra;
    @Column(name = "level")
    private int level;
    @Column(name = "weight")
    private int weight;

    public CDCData() { }

    public CDCData(CDCDataKey cdcDataKey, String diseaseExtra, String locationExtra, int level, int weight) {
        this.cdcDataKey = cdcDataKey;
        this.diseaseExtra = diseaseExtra;
        this.locationExtra = locationExtra;
        this.level = level;
        this.weight = weight;
    }

    public CDCDataKey getCdcDataKey() {
        return cdcDataKey;
    }

    public void setCdcDataKey(CDCDataKey cdcDataKey) {
        this.cdcDataKey = cdcDataKey;
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
        return new Object[]{this.cdcDataKey};
    }

    @Override
    public CDCDataKey getKey(){
        return this.cdcDataKey;
    }

    public static int getWeightFromLevel(int level){
        if(level == 1) return 100;
        else if (level == 2) return 250;
        else if (level == 3) return 500;
        else return 0;
    }

    public String getName() {
        return this.cdcDataKey.getDisease();
    }

    public String getLocation() {
        return this.cdcDataKey.getLocation();
    }

    public String getDate() {
        return this.cdcDataKey.getDate();
    }
}
