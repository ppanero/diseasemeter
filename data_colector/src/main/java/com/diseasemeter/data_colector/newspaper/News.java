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

    public News(String url, String title, String topic, String content) {
        this.url = url;
        this.title = title;
        this.topic = topic;
        this.content = content;
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
}
