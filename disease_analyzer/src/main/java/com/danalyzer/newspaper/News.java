package com.danalyzer.newspaper;

/**
 * Created by Light on 16/12/15.
 */
public class News {

    private String url;
    private String title;
    private String content;

    public News(String url, String title, String content) {
        this.url = url;
        this.title = title;
        this.content = content;
    }

    public String getUrl(){
        return url;
    }

    public String getTitulo() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
