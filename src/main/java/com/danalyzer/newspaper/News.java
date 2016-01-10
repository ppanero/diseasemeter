package com.danalyzer.newspaper;

/**
 * Created by Light on 16/12/15.
 */
public abstract class News {

    protected String url;
    protected String title;
    protected String content;

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
