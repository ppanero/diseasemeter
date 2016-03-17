package com.ppanero.disease_checker;

/**
 * Created by Light on 14/03/16.
 */
public class DiseaseItem {

    private String name;
    private DiseaseLevel level;
    private String locationDate;
    private int twitterCount;
    private int newsCount;
    private int cdcLevel;
    private int twitterIcon;
    private int newsIcon;
    private int cdcIcon;

    public DiseaseItem(String name, DiseaseLevel level, String locationDate, int twitterCount, int cdcLevel, int newsCount) {
        this.name = name;
        this.level = level;
        this.locationDate = locationDate;
        this.twitterCount = twitterCount;
        this.cdcLevel = cdcLevel;
        this.newsCount = newsCount;
    }

    public DiseaseItem(String name, DiseaseLevel level, String locationDate, int twitterCount,
                       int newsCount, int cdcLevel, int twitterIcon, int newsIcon, int cdcIcon) {
        this.name = name;
        this.level = level;
        this.locationDate = locationDate;
        this.twitterCount = twitterCount;
        this.newsCount = newsCount;
        this.cdcLevel = cdcLevel;
        this.twitterIcon = twitterIcon;
        this.newsIcon = newsIcon;
        this.cdcIcon = cdcIcon;
    }

    public String getName() {
        return name;
    }

    public DiseaseLevel getLevel() {
        return level;
    }

    public String getLocationDate() {
        return locationDate;
    }

    public int getTwitterCount() {
        return twitterCount;
    }

    public int getNewsCount() {
        return newsCount;
    }

    public int getCdcLevel() {
        return cdcLevel;
    }

    public int getNewsIcon() {
        return newsIcon;
    }

    public int getTwitterIcon() {
        return twitterIcon;
    }

    public int getCdcIcon() {
        return cdcIcon;
    }
}
