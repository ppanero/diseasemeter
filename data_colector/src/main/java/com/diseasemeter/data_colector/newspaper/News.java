package com.diseasemeter.data_colector.newspaper;

import java.io.Serializable;

/**
 * Created by Light on 16/12/15.
 */
public class News implements Serializable{

    private String url;
    private String title;
    private String topic;
    private String content;
    private int language;
    private int sentiment;
    private int weight;

    public News() {}

    public News(String url, String title, String topic, String content) {
        this.url = url;
        this.title = title;
        this.topic = topic;
        this.content = content;
        this.language = -1;
        this.sentiment = -1;
        this.weight = -1;
    }

    public News(String url, String title, String topic, String content, int language, int sentiment, int weight) {
        this.url = url;
        this.title = title;
        this.topic = topic;
        this.content = content;
        this.language = language;
        this.sentiment = sentiment;
        this.weight = weight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public static int getWeightFromSentiment(int sentiment){
        switch (sentiment){
            case -1: return -2;
            case 0: return 2;
            case 1: return 4;
            default: return 2;
        }
    }
}
