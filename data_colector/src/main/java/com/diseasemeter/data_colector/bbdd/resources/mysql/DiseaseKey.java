package com.diseasemeter.data_colector.bbdd.resources.mysql;


import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Light on 18/04/16.
 */
public class DiseaseKey implements Serializable{

    private String _name;
    private String _location;

    public DiseaseKey() {
    }

    public DiseaseKey(String _name, String _location) {
        this._name = _name;
        this._location = _location;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String _location) {
        this._location = _location;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof DiseaseKey) ) return false;

        final DiseaseKey oKey = (DiseaseKey) other;

        if ( !oKey.getLocation().equals( getLocation() ) ) return false;
        if ( !oKey.getName().equals( getName() ) ) return false;

        return true;
    }

    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(_name);
        hcb.append(_location);

        return hcb.toHashCode();
    }
}
