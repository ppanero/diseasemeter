package com.diseasemeter.data_colector.common;

/**
 * Created by Light on 19/04/16.
 */
public class UtilsSpatial {

    public static boolean isValidLngLat(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }
}
