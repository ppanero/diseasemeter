package com.diseasemeter.data_colector.bbdd.resources.mysql;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Light on 18/04/16.
 */
public class CDCDataKey implements Serializable{

    private String _disease;
    private String _location;
    private String _date;

    public CDCDataKey() {
    }

    public CDCDataKey(String name, String location, String date) {
        this._disease = name;
        this._location = location;
        this._date = date;
    }

    public String getDisease() {
        return _disease;
    }

    public void setDisease(String name) {
        this._disease = name;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        this._location = location;
    }

    public String getDate() {
        return _date;
    }

    public void setDate(String date) {
        this._date = date;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof CDCDataKey) ) return false;

        final CDCDataKey oKey = (CDCDataKey) other;

        if ( !oKey.getDate().equals( getDate() ) ) return false;
        if ( !oKey.getLocation().equals( getLocation() ) ) return false;
        if ( !oKey.getDisease().equals( getDisease() ) ) return false;

        return true;
    }

    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(_disease);
        hcb.append(_location);
        hcb.append(_date);

        return hcb.toHashCode();
    }
}
