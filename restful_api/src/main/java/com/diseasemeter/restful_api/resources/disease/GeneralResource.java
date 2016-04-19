package com.diseasemeter.restful_api.resources.disease;

import java.io.Serializable;

/**
 * Created by Light on 17/04/16.
 */
public abstract class GeneralResource<T extends Serializable>{

    public abstract Object[] getKeyValues();

    public abstract T getKey();
}
