package com.diseasemeter.data_colector.bbdd.resources.mysql;


import com.diseasemeter.data_colector.common.UtilsCommon;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Light on 07/04/16.
 */
@Entity
@Table(name = "disease")
public class Disease extends GeneralResource<DiseaseKey>  implements Serializable{

    private static final String outputDateFormat = "dd/MM/yyyy";

    @EmbeddedId
    private DiseaseKey diseaseKey;
    @Column(name = "initial_date")
    private String initialDate;
    @Column(name = "last_update")
    private String lastUpdate;
    @Column(name = "level")
    private int level;
    @Column(name = "weight")
    private int weight;
    @Column(name = "twitter_count")
    private int tweetsCount;
    @Column(name = "news_count")
    private int newsCount;
    @Column(name = "cdc_count")
    private int cdcCount;
    @Column(name = "active")
    private Boolean isActive;


    public Disease() {}

    public Disease(DiseaseKey diseaseKey) {
        this.diseaseKey = diseaseKey;
        this.initialDate = UtilsCommon.getCurrentDate(outputDateFormat);
        this.lastUpdate = UtilsCommon.getCurrentDate(outputDateFormat);
        this.level = 1;
        this.weight = 0;
        this.tweetsCount = 0;
        this.newsCount = 0;
        this.cdcCount = 0;
        this.isActive = true;
    }

    public Disease(DiseaseKey diseaseKey, String initialDate, String lastUpdate, int level,
                   int weight, int tweetsCount, int newsCount, int cdcCount, Boolean isActive) {
        this.diseaseKey = diseaseKey;
        this.initialDate = initialDate;
        this.lastUpdate = lastUpdate;
        this.level = level;
        this.weight = weight;
        this.tweetsCount = tweetsCount;
        this.newsCount = newsCount;
        this.cdcCount = cdcCount;
        this.isActive = isActive;
    }

    public DiseaseKey getDiseaseKey() {
        return diseaseKey;
    }

    public void setDiseaseKey(DiseaseKey diseaseKey) {
        this.diseaseKey = diseaseKey;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    public int getTweetsCount() {
        return tweetsCount;
    }

    public void setTweetsCount(int tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
    }

    public int getCdcCount() {
        return cdcCount;
    }

    public void setCdcCount(int cdcCount) {
        this.cdcCount = cdcCount;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public Object[] getKeyValues() {
        return new Object[]{this.diseaseKey};
    }

    @Override
    public DiseaseKey getKey(){
        return this.diseaseKey;
    }

    @Override
    public String toString(){
        return "Name: ".concat(this.diseaseKey.getName()).concat("\n").concat(
                "Location: ").concat(this.diseaseKey.getLocation()).concat("\n");
    }

    public static int getLevelFromNewWeight(int nWeight){
        if(nWeight < 250) return 1;
        else if (nWeight >= 250 && nWeight < 500) return 2;
        else return 3;
    }
}
