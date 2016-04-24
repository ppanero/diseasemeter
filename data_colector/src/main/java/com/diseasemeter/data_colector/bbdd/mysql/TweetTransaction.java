package com.diseasemeter.data_colector.bbdd.mysql;

import com.diseasemeter.data_colector.bbdd.resources.mysql.Tweet;

/**
 * Created by Light on 24/04/16.
 */
public class TweetTransaction extends GeneralTransaction<Tweet>{

    private static final String[] KEY_COLUMN_NAMES = {"tweetKey"};

    @Override
    public Object[] getKeyColumnNames() {
        return new Object[]{KEY_COLUMN_NAMES};
    }
}
