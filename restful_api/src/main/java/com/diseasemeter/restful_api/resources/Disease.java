package com.diseasemeter.restful_api.resources;

/**
 * Created by Light on 07/04/16.
 */
public class Disease {

    private String name;
    private String place;
    private String date;
    private String level;
    private int tweets;
    private int news;
    private int cdc;

    public Disease(String name, String place, String date, String level, int tweets, int news, int cdc) {
        this.name = name;
        this.place = place;
        this.date = date;
        this.level = level;
        this.tweets = tweets;
        this.news = news;
        this.cdc = cdc;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getLevel() {
        return level;
    }

    public int getTweets() {
        return tweets;
    }

    public int getNews() {
        return news;
    }

    public int getCdc() {
        return cdc;
    }
}
