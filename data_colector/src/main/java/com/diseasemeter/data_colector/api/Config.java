package com.diseasemeter.data_colector.api;



import com.diseasemeter.data_colector.common.UtilsCommon;

import java.util.Properties;

/**
 * Created by Light on 17/03/16.
 */
public class Config {

    private static String API_KEY;

    public static boolean config(){
        Properties tProp = UtilsCommon.readProperties("monkeylearn.properties");
        API_KEY = tProp.getProperty("apiToken");

        return !(API_KEY == null || API_KEY.equals(""));
    }

    public static String getApiKey() {
        return API_KEY;
    }
}
