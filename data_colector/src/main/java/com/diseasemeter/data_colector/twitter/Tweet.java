package com.diseasemeter.data_colector.twitter;

import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import twitter4j.Place;
import twitter4j.User;

import java.util.Date;

/**
 * Contains the creation place, user's default location, creation country's name, creation place's name,
 * creation place's type, tweet text content, and tweet source.
 */
public class Tweet {

    private static final String inputDateFromat = "EEE MMM d HH:mm:ss z yyyy";
    private static final String outputDateFormat = "yyyy-MM-dd HH:mm:ss";
    private String creationTime;
    private String creationCountry;
    private String creationPlaceName;
    private String creationPlaceType;
    private String content;
    private String userDefaultLocation;
    private String userName;

    public Tweet(Date createdAt, Place place, String text, User user) {
        if(createdAt != null){
            creationTime = UtilsCommon.formatDate(createdAt.toString(), inputDateFromat, outputDateFormat);
        }
        else{
            creationTime = MACRO.MISSING_VALUE;
        }
        if(place != null){
            this.creationCountry = place.getCountry();
            this.creationPlaceName = place.getName();
            this.creationPlaceType = place.getPlaceType();
        }
        else{
            this.creationCountry = MACRO.MISSING_VALUE;
            this.creationPlaceName = MACRO.MISSING_VALUE;
            this.creationPlaceType = MACRO.MISSING_VALUE;
        }
        if(text != null){
            this.content = text; //In case there is a \t in the text
        }
        else{
            this.content = MACRO.MISSING_VALUE;
        }
        if(user != null){
            this.userDefaultLocation = user.getLocation();
            this.userName = user.getName();
        }
        else{
            this.content = MACRO.MISSING_VALUE;
            this.userName = MACRO.MISSING_VALUE;
        }
    }

    public String getCreationTime() {
        return (creationTime != null)? creationTime: MACRO.MISSING_VALUE;
    }

    public String getCreationCountry() {
        return (creationCountry != null)? creationCountry: MACRO.MISSING_VALUE;
    }

    public String getCreationPlaceName() {
        return (creationPlaceName != null)? creationPlaceName: MACRO.MISSING_VALUE;
    }

    public String getCreationPlaceType() {
        return (creationPlaceType != null)? creationPlaceType: MACRO.MISSING_VALUE;
    }

    public String getContent() {
        return (content != null)? content: MACRO.MISSING_VALUE;
    }

    public String getUserDefaultLocation() {
        return (userDefaultLocation != null)? userDefaultLocation: MACRO.MISSING_VALUE;
    }

    public String getUserName(){
        return (userName != null)? userName: MACRO.MISSING_VALUE;
    }
}
