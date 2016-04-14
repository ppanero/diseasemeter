package com.diseasemeter.restful_api.resources.disease;

import javax.persistence.*;

/**
 * Created by Light on 07/04/16.
 */
@Entity
@Table(name = "disease")
public class Disease {


    @Id @GeneratedValue
    @Column(name = "_id")
    private int _id;
    @Column(name = "name")
    private String name;
    @Column(name = "location")
    private String location;
    @Column(name = "initial_date")
    private String initialDate;
    @Column(name = "last_update")
    private String lastUpdate;
    @Column(name = "level")
    private int level;
    @Column(name = "twitter_count")
    private int tweetsCount;
    @Column(name = "news_count")
    private int newsCount;
    @Column(name = "cdc_count")
    private int cdcCount;
    @Column(name = "active")
    private Boolean isActive;


    public Disease() {}

    public Disease(int _id, String name, String location, String initialDate, String lastUpdate, int level,
                   int tweetsCount, int newsCount, int cdcCount, boolean isActive) {
        this._id = _id;
        this.name = name;
        this.location = location;
        this.initialDate = initialDate;
        this.lastUpdate = lastUpdate;
        this.level = level;
        this.tweetsCount = tweetsCount;
        this.newsCount = newsCount;
        this.cdcCount = cdcCount;
        this.isActive = isActive;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
