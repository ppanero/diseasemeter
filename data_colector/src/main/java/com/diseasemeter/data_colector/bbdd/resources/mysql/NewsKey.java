package com.diseasemeter.data_colector.bbdd.resources.mysql;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Light on 23/04/16.
 */
public class NewsKey implements Serializable{

    private String _title;
    private String _disease;

    public NewsKey() {
    }

    public NewsKey(String _title, String _disease) {
        this._title = _title;
        this._disease = _disease;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public String getDisease() {
        return _disease;
    }

    public void setDisease(String _disease) {
        this._disease = _disease;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof NewsKey) ) return false;

        final NewsKey oKey = (NewsKey) other;

        if ( !oKey.getTitle().equals( getTitle() ) ) return false;
        if ( !oKey.getDisease().equals( getDisease() ) ) return false;

        return true;
    }

    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(_title);
        hcb.append(_disease);

        return hcb.toHashCode();
    }


}
