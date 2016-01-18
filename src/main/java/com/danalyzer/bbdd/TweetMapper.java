package com.danalyzer.bbdd;

import com.danalyzer.common.MACRO;
import com.danalyzer.twitter.Tweet;

/**
 * Created by Light on 02/01/16.
 */
public class TweetMapper extends GenericMapper<Tweet>{

    private String TABLE_NAME;
    private String[] COLUMN_NAMES;

    public TweetMapper(HiveJdbcClient jdbcClient, String tableName , String[] columnNames) {
        super(jdbcClient);
        this.TABLE_NAME = tableName;
        this.COLUMN_NAMES = columnNames;
    }

    @Override
    public String getTableName() {
        return this.TABLE_NAME;
    }

    @Override
    public String[] getColumnNames() {
        return this.COLUMN_NAMES;
    }

    @Override
    protected String[] getSerializedObjectAsString(Tweet tweet) {
        //TODO: take status and disease
        return new String[]{MACRO.QUOTES.concat(tweet.getCreationTime()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat(tweet.getCreationCountry()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat(tweet.getCreationPlaceName()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat(tweet.getCreationPlaceType()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat(tweet.getContent()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat(tweet.getUserDefaultLocation()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat(tweet.getUserName()).concat(MACRO.QUOTES),
                MACRO.QUOTES.concat("\\N").concat(MACRO.QUOTES),
                MACRO.QUOTES.concat("\\N").concat(MACRO.QUOTES)};
    }
}
