package com.diseasemeter.data_colector.twitter;


import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsFS;
import com.diseasemeter.data_colector.common.UtilsSpark;
import com.diseasemeter.data_colector.common.UtilsTwitter;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import twitter4j.Status;

import java.io.Serializable;
import java.util.*;

/**
 * This class implements a process of a twitter stream. It will create and process the stream according
 * to the given parameters: -o for the output directory, -f for a comma separated list of filters to apply to the stream,
 * -m to specify the spark masters (if it is not valid, it will be set to local[2]), and -t for the interval in milliseconds.
 *
 * This will take the creation time, country, place, and place type, the content, the default user location, the user name
 * and will add the filters that match the content of the tweet. Then, they will be saved to the ouput directy by using a
 * temporary one to avoid the "FileAlreadyExists" error.
 *
 * If a value is missing in the tweet, the character \N (null character) will be written instead. In addition, all \n
 * (end of line) characters will be substituted by spaces in the resulting string, trimming it to avoid
 * spaces at the beginning or the end of it.
 *
 * Values are separated by \t (tabulator character).
 */
public class TwitterStreamer implements Serializable {


    private static Logger log = Logger.getLogger(TwitterStreamer.class);
    private static final String[] FILES_TO_AVOID = {"SUCCESS", "CRC" ,"crc"};
    private Function<JavaRDD<String>, Void> saveFileFunction;
    private FlatMapFunction<Status, String> tweetFormatter;
    private Broadcast<String> bTmpDir;
    private Broadcast<String> bOutputDir;
    private Broadcast<String[]> bFilters;

    public String getbOutputDir() {
        return bOutputDir.getValue();
    }

    public void setbOutputDir(Broadcast<String> bOutputDir) {
        this.bOutputDir = bOutputDir;
    }

    public String getbTmpDir() {
        return bTmpDir.getValue();
    }

    public void setbTmpDir(Broadcast<String> bTmpDir) {
        this.bTmpDir = bTmpDir;
    }

    public String[] getbFilters() {
        return bFilters.getValue();
    }

    public void setbFilters(Broadcast<String[]> filters) {
        this.bFilters = filters;
    }

    public static void main(String[] args) {

        String outpurDir = "", sparkMaster = "";
        Set<String> fSet = new HashSet<String>();
        int interval = MACRO.TWEETER_DEFAULT_INTERVAL;
        //Read input arguments
        if (args.length != 8) {
            System.out.printf("Usage: TwitterStreamer -o <output dir> -f <commma separated list of filters>" +
                    "-m <spark master [ local[n] | yarn-client |  yarn-cluster ] > " +
                    "-t <tweets' time interval in milliseconds> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        addOptions(options);
        TwitterStreamer twitterStreamer = new TwitterStreamer();
        try {
            cmd = parser.parse( options, args);
        }catch (ParseException e) {
            log.error("Exit program with code (-2). Error parsing options");
            System.exit(-2);
        }
        if(cmd != null){
            if(cmd.hasOption("o")) {
                String outVal = cmd.getOptionValue("o");
                if(outVal != null){
                    outpurDir = UtilsFS.preparePath(outVal, false);
                    log.debug("Output directory set to: " + outpurDir);
                }
            }
            if(cmd.hasOption("t")) {
                String tInterval = cmd.getOptionValue("t");
                if(tInterval != null){
                    interval = Integer.parseInt(tInterval);
                    log.debug("Streaming interval set to: " + interval + " milliseconds");
                }
            }
            if(cmd.hasOption("m")) {
                String sMaster = cmd.getOptionValue("m");
                if(sMaster != null){
                    if(UtilsSpark.checkSparkMaster(sMaster)) {
                        sparkMaster = sMaster;
                    }
                    else{
                        sparkMaster = MACRO.TWEETER_STREAMER_DEFAULT_MASTER;
                    }
                    log.debug("Spark master set to: " + sparkMaster);
                }
            }
            if(cmd.hasOption("f")) {
                String filtersLine = cmd.getOptionValue("f");
                if(filtersLine != null){
                    fSet = UtilsTwitter.parseLine(filtersLine);
                }
            }
        }

        twitterStreamer.run(outpurDir, interval, sparkMaster, fSet);
        log.debug("Exit program with code (0)");
        System.exit(-0);
    }

    private void run(String outputDir, int interval, String sparkMaster, Set<String> fSet) {

        SparkConf conf = new SparkConf().setAppName("Disease Analyzer: Twitter Streamer [Beca Colaboracion UCM]").
                set("spark.shuffle.consolidateFiles", "false").setMaster(sparkMaster);
        init();

        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(interval));
        log.debug("Streaming context created successfully");
        setbOutputDir(ssc.sparkContext().broadcast(outputDir));
        setbTmpDir(ssc.sparkContext().broadcast(UtilsFS.tmpDir(outputDir, MACRO.TMP_DIR_NAME)));
        setbFilters(ssc.sparkContext().broadcast(fSet.toArray(new String[fSet.size()])));
        log.debug("Broadcasts set successfully");
        JavaInputDStream<Status> tweets = TwitterUtils.createStream(ssc,
                UtilsTwitter.readTwitterCredentials("twitter4j.properties"), getbFilters());
        log.debug("Stream created successfully");
        process(tweets);
        log.info("Twitter streamer is about to start...");
        ssc.start();
        ssc.awaitTermination();
        //Clean the tmp files directory of possible remaining files due to interruption of the thread
        UtilsFS.cleanDirectory(getbTmpDir());
        log.info("Cleaning temporal directory");
    }

    private void init() {

        saveFileFunction = new Function<JavaRDD<String>, Void>(){

            public Void call(JavaRDD<String> stringJavaRDD) throws Exception {
                if(!stringJavaRDD.isEmpty()) {
                    String currentMillis = String.valueOf(System.currentTimeMillis());
                    String auxPath = getbTmpDir().concat(currentMillis);
                    String outDir = getbOutputDir();
                    String markdown = MACRO.TWITTER_FILE_TAG.concat(MACRO.UNDERSCORE).concat(currentMillis);
                    stringJavaRDD.saveAsTextFile(auxPath);
                    UtilsFS.MoveAndDelete(auxPath, outDir, markdown, MACRO.EMPTY, Arrays.asList(FILES_TO_AVOID) , true);
                }
                return null;
            }
        };

        /**
         * Takes the creation place, user's default location, creation country's name, creation place's name,
         * creation place's type, tweet text content, and tweet source.
         */
        tweetFormatter = new FlatMapFunction<Status, String>() {
            @Override
            public Iterable<String> call(Status status) throws Exception {
                Set<String> ret = new HashSet<String>();
                Tweet tweet = new Tweet(status.getCreatedAt(),status.getPlace(), status.getText(), status.getUser());
                String baseString = tweet.getCreationTime().concat(MACRO.FILE_SEPARATOR)
                        .concat(tweet.getCreationCountry()).concat(MACRO.FILE_SEPARATOR)
                        .concat(tweet.getCreationPlaceName()).concat(MACRO.FILE_SEPARATOR)
                        .concat(tweet.getCreationPlaceType()).concat(MACRO.FILE_SEPARATOR)
                        .concat(tweet.getContent()).concat(MACRO.FILE_SEPARATOR)
                        .concat(tweet.getUserDefaultLocation()).concat(MACRO.FILE_SEPARATOR)
                        .concat(tweet.getUserName().concat(MACRO.FILE_SEPARATOR));

                Set<String> filters = UtilsTwitter.getFilter(tweet.getContent(), getbFilters());
                if(filters.isEmpty()){
                    ret.add(baseString.concat(MACRO.MISSING_VALUE).replaceAll(MACRO.END_OF_LINE, MACRO.SPACE).trim());
                }
                else {
                    for (String filter : filters) {
                        ret.add(baseString.concat(filter).replaceAll(MACRO.END_OF_LINE, MACRO.SPACE).trim());
                    }
                }
                log.debug("Tweet processed with " + filters.size() + " filters");
                return ret;
            }
        };
    }

    private void process(JavaDStream tweets) {
        tweets.flatMap(tweetFormatter).foreachRDD(saveFileFunction);
    }

    private static void addOptions(Options options) {
        options.addOption("o", true, "output directory");
        options.addOption("t", true, "time interval for twitter streamer");
        options.addOption("m", true, "spark master");
        options.addOption("f", true, "list of comma separated words to filter by");
    }
}
