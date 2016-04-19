package com.diseasemeter.restful_api.resources.disease;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Light on 07/04/16.
 */
@Entity
@Table(name = "disease")
public class Disease extends GeneralResource<DiseaseKey>  implements Serializable{

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
    @JsonIgnore
    public Object[] getKeyValues() {
        return new Object[]{this.diseaseKey};
    }

    @Override
    @JsonIgnore
    public DiseaseKey getKey(){
        return this.diseaseKey;
    }

}
