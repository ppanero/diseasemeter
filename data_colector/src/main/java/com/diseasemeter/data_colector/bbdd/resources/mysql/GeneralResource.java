package com.diseasemeter.data_colector.bbdd.resources.mysql;

import java.io.Serializable;

/**
 * Created by Light on 17/04/16.
 */
public abstract class GeneralResource<T extends Serializable>{

    public abstract Object[] getKeyValues();

    public abstract T getKey();
}
