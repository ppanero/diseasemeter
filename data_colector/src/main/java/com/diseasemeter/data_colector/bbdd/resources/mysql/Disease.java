package com.diseasemeter.data_colector.bbdd.resources.mysql;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Light on 07/04/16.
 */
@Entity
@Table(name = "disease")
public class Disease extends GeneralResource  implements Serializable{

    @Id
    @Column(name = "_name")
    private String _name;
    @Id
    @Column(name = "_location")
    private String _location;
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

    public Disease(String name, String location, String initialDate, String lastUpdate, int level,
                   int tweetsCount, int newsCount, int cdcCount, boolean isActive) {
        this._name = name;
        this._location = location;
        this.initialDate = initialDate;
        this.lastUpdate = lastUpdate;
        this.level = level;
        this.tweetsCount = tweetsCount;
        this.newsCount = newsCount;
        this.cdcCount = cdcCount;
        this.isActive = isActive;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_location() {
        return _location;
    }

    public void set_location(String _location) {
        this._location = _location;
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

    @Override
    public Object[] getKeyValues() {
        return new Object[]{this._name, this._location};
    }
}
