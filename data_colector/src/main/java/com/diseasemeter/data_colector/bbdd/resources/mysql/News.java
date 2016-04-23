package com.diseasemeter.data_colector.bbdd.resources.mysql;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Light on 16/12/15.
 */
@Entity
@Table(name = "news")
public class News extends GeneralResource<NewsKey>  implements Serializable{

    @EmbeddedId
    private NewsKey newsKey;
    @Column(name = "source")
    private String source;
    @Column(name = "language")
    private int language;
    @Column(name = "sentiment")
    private int sentiment;
    @Column(name = "weight")
    private int weight;
    @Column(name = "content")
    private String content;
    @Column(name = "topic")
    private String topic;
    @Column(name = "url")
    private String url;
    @Column(name = "location")
    private String location;
    @Column(name = "date")
    private String date;

    public News() {}

    public News(NewsKey newsKey, String source, int language, int sentiment, int weight, String content,
                String topic, String url, String location, String date) {
        this.newsKey = newsKey;
        this.source = source;
        this.language = language;
        this.sentiment = sentiment;
        this.weight = weight;
        this.content = content;
        this.topic = topic;
        this.url = url;
        this.location = location;
        this.date = date;
    }

    public News(NewsKey newsKey, String url, String topic, String source) {
        this.newsKey = newsKey;
        this.url = url;
        this.topic = topic;
        this.source = source;
        this.language = -1;
        this.sentiment = -1;
        this.weight = 0;
        this.content = null;
        this.location = null;
        this.date = null;

    }

    public NewsKey getNewsKey() {
        return newsKey;
    }

    public void setNewsKey(NewsKey newsKey) {
        this.newsKey = newsKey;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public static int getWeightFromSentiment(int sentiment){
        switch (sentiment){
            case -1: return -2;
            case 0: return 2;
            case 1: return 4;
            default: return 2;
        }
    }

    @Override
    public Object[] getKeyValues() {
        return new Object[]{this.newsKey};
    }

    @Override
    public NewsKey getKey() {
        return this.newsKey;
    }

    @Override
    public String toString(){
        String location = "NO LOCATION";
        if(this.location != null)
            location = this.location;

        String disease = "NO DISEASE";
        if(this.newsKey.getDisease() != null)
            disease = this.newsKey.getDisease();

        return "Title: ".concat(this.newsKey.getTitle()).concat("\n").concat(
                "Disease: ").concat(disease).concat("\n").concat(
                "Location: ").concat(location).concat("\n");
    }
}
