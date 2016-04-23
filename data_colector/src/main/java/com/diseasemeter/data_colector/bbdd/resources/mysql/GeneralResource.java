package com.diseasemeter.data_colector.bbdd.resources.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by Light on 17/04/16.
 */
public abstract class GeneralResource<T extends Serializable>{

    @JsonIgnore
    public abstract Object[] getKeyValues();

    @JsonIgnore
    public abstract T getKey();
}
