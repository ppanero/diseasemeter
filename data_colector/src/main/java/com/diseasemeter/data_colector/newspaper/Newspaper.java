package com.danalyzer.newspaper;

import com.danalyzer.common.UtilsFS;

import java.util.Set;


/**
 * Created by Light on 13/12/15.
 */
public abstract class Newspaper {

    protected String url;

    public Newspaper(){
        this.url = "";
    }

    public Newspaper(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    protected boolean saveNews(Set<String> news, String outputDir, String filename ){
        return UtilsFS.saveFile(outputDir, filename, news);
    }

    public abstract Set<String> getWebConent();


}
