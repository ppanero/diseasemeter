package com.danalyzer.newspaper;

import java.util.List;


/**
 * Created by Light on 13/12/15.
 */
public abstract class Newspaper {

    protected String url;
    
    public Newspaper(String url) {
        this.url = url;
    }

    public abstract List<News> getWebConent();
}
