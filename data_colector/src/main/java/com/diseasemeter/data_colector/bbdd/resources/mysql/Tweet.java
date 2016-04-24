package com.diseasemeter.data_colector.bbdd.resources.mysql;

import com.diseasemeter.data_colector.common.UtilsCommon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import twitter4j.Place;
import twitter4j.User;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Contains the creation place, user's default location, creation country's name, creation place's name,
 * creation place's type, tweet text content, and tweet source.
 */
@Entity
@Table(name = "twitter_data")
public class Tweet extends GeneralResource<TweetKey>  implements Serializable {

    private static final String inputDateFromat = "EEE MMM d HH:mm:ss z yyyy";
    private static final String outputDateFormat = "dd/MM/yyyy";

    @EmbeddedId
    private TweetKey tweetKey;
    @Column(name = "creation_time")
    private String creationTime;
    @Column(name = "creation_country")
    private String creationCountry;
    @Column(name = "creation_place_name")
    private String creationPlaceName;
    @Column(name = "creation_place_type")
    private String creationPlaceType;
    @Column(name = "user_location")
    private String userDefaultLocation;
    @Column(name = "language")
    private int language;
    @Column(name = "sentiment")
    private int sentiment;
    @Column(name = "weight")
    private int weight;


    public Tweet() { }

    public Tweet(TweetKey tweetKey, String creationTime, String creationCountry, String creationPlaceName,
                 String creationPlaceType, String userDefaultLocation, int language, int sentiment, int weight) {
        this.tweetKey = tweetKey;
        this.creationTime = creationTime;
        this.creationCountry = creationCountry;
        this.creationPlaceName = creationPlaceName;
        this.creationPlaceType = creationPlaceType;
        this.userDefaultLocation = userDefaultLocation;
        this.language = language;
        this.sentiment = sentiment;
        this.weight = weight;
    }

    public Tweet(Date createdAt, Place place, String text, User user) {
        this.tweetKey = new TweetKey(text, user.getName(), null);
        this.userDefaultLocation = user.getLocation();
        if(createdAt != null){
            creationTime = UtilsCommon.formatDate(createdAt.toString(), inputDateFromat, outputDateFormat);
        }
        else{
            creationTime = null;
        }
        if(place != null){
            this.creationCountry = place.getCountry();
            this.creationPlaceName = place.getName();
            this.creationPlaceType = place.getPlaceType();
        }
        else {
            this.creationCountry = null;
            this.creationPlaceName = null;
            this.creationPlaceType = null;
        }
        this.sentiment = 0;
        this.language = 0;
        this.weight = 0;
    }

    public static String getInputDateFromat() {
        return inputDateFromat;
    }

    public static String getOutputDateFormat() {
        return outputDateFormat;
    }

    public TweetKey getTweetKey() {
        return tweetKey;
    }

    public void setTweetKey(TweetKey tweetKey) {
        this.tweetKey = tweetKey;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getCreationCountry() {
        return creationCountry;
    }

    public void setCreationCountry(String creationCountry) {
        this.creationCountry = creationCountry;
    }

    public String getCreationPlaceName() {
        return creationPlaceName;
    }

    public void setCreationPlaceName(String creationPlaceName) {
        this.creationPlaceName = creationPlaceName;
    }

    public String getCreationPlaceType() {
        return creationPlaceType;
    }

    public void setCreationPlaceType(String creationPlaceType) {
        this.creationPlaceType = creationPlaceType;
    }

    public String getUserDefaultLocation() {
        return userDefaultLocation;
    }

    public void setUserDefaultLocation(String userDefaultLocation) {
        this.userDefaultLocation = userDefaultLocation;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public Object[] getKeyValues() {
        return new Object[]{tweetKey};
    }

    @Override
    public TweetKey getKey() {
        return tweetKey;
    }

    public boolean noLocation(){
        return this.creationCountry == null && this.creationPlaceName == null && this.userDefaultLocation == null;
    }

    @JsonIgnore
    public String getPreferredLocation(){
        if(this.creationPlaceName != null) return this.creationPlaceName;
        if(this.creationCountry != null) return this.creationCountry;
        if(this.userDefaultLocation != null) return this.userDefaultLocation;
        return null;
    }

    @JsonIgnore
    public static int getWeightFromSentiment(int sentiment){
        switch (sentiment){
            case -1: return -2;
            case 0: return 2;
            case 1: return 4;
            default: return 2;
        }
    }
}
