package com.diseasemeter.data_colector.bbdd;

/**
 * Created by Light on 02/01/16.
 */
public class BBDDMACRO {

    //----TABLES----
    public static final String TWITTER_USERS_TABLE_NAME = "twitter_users";
    public static final String TWITTER_STREAMING_TABLE_NAME = "twitter_streaming";

    //----COLUMNS----
    public static final String[] TWITTER_USERS_TABLE_COLUMNS = {"creation_time", "creation_country", "creation_place_name",
                                   "creation_place_type",  "content", "user_default_location", "username", "disease", "status"};
    public static final String[] TWITTER_STREAMING_COLUMNS = {"creation_time", "creation_country", "creation_place_name",
            "creation_place_type",  "content", "user_default_location", "username", "tag"};


}
