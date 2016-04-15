package com.diseasemeter.data_colector.common;

/**
 * Created by Light on 15/12/15.
 */
public class MACRO {

    //----SYSTEM----
    public static final String END_OF_LINE = System.getProperty("line.separator");
    public static final String PATH_SEPARATOR = System.getProperty("file.separator");
    public static final String FILE_SEPARATOR = "\t";
    public static final String MISSING_VALUE = "\\N";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String HIDDEN_FILES_CHARACTER = ".";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String EMPTY = "";
    public static final String TMP_DIR_NAME = "tmp";
    public static final String QUOTES = "\"";

    //----TWITTER----
    public static final String TWITTER_FILE_TAG = "twitter_stream";
    public static final int TWEETER_DEFAULT_INTERVAL = 60000; //60 seconds
    public static final String TWEETER_STREAMER_DEFAULT_MASTER = "local[2]";

    //----DATABASE----
    public static final String BBDD_PROPERTIES_FILE = "bbdd.properties";
    //----HIVE----
    public static final String HIVE_DRIVER = "hive.driver.jdbc";
    public static final String HIVE_IP = "hive.ip";
    public static final String HIVE_USER = "hive.user";
    public static final String HIVE_PORT = "hive.port";
    public static final String HIVE_DATABASE = "hive.database";
}
