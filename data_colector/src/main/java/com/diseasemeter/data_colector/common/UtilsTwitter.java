package com.diseasemeter.data_colector.common;


import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.PropertyConfiguration;

import java.util.*;

/**
 * Created by Light on 30/12/15.
 */
public class UtilsTwitter {


    public static Authorization readTwitterCredentials(String file) {
        return new OAuthAuthorization(new PropertyConfiguration(UtilsCommon.readProperties(file)));
    }

    public static Set<String> parseLine(String line) {
        String[] lineParts = line.split(",");
        Set<String> ret = new HashSet<String>();

        for(String part : lineParts){
            if(part != null)
                ret.add(part.trim());
        }
        return ret;
    }

    /**
     * Matches the content with all the filters given in the String array and returns the matching ones as a set.
     * @param content
     * @param strings
     * @return
     */
    public static Set<String> getFilter(String content, String[] strings) {

        Set<String> filterSet = new HashSet<String>();
        for(String filter : strings){
            if(content.toLowerCase().contains(filter.toLowerCase())){
                filterSet.add(filter);
            }
        }
        return filterSet;
    }
}
