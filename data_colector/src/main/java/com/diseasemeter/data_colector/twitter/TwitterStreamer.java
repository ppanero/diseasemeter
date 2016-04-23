package com.diseasemeter.data_colector.twitter;


import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.common.*;
import com.diseasemeter.data_colector.microsoft_api.Translator;
import com.diseasemeter.data_colector.monkey_learn.Processor;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
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
    private Broadcast<String[]> bFilters;

    //Spark functions
    private FlatMapFunction<Disease,String> processDiseaseNames;
    private FlatMapFunction<String,String> addTranslations;
    private Function<JavaRDD<String>, Void> saveFileFunction;
    private FlatMapFunction<Status, String> tweetFormatter;

    public String[] getbFilters() {
        return bFilters.getValue();
    }

    public void setbFilters(Broadcast<String[]> filters) {
        this.bFilters = filters;
    }

    public static void main(String[] args) {

        String sparkMaster = "";
        int interval = MACRO.TWEETER_DEFAULT_INTERVAL;
        //Read input arguments
        if (args.length != 4) {
            System.out.printf("Usage: TwitterStreamer" +
                    "-m <spark master [ local[n] | yarn-client |  yarn-cluster ] > " +
                    "-t <tweets' time interval in milliseconds> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        addOptions(options);
        TwitterStreamer twitterStreamer = new TwitterStreamer();

        Map<String, String> parsedArgs = UtilsCommon.getArgs(options, args);

        String tInterval = parsedArgs.get("t");
        if(tInterval != null){
            interval = Integer.parseInt(tInterval);
            log.debug("Streaming interval set to: " + interval + " milliseconds");
        }

        String sMaster = parsedArgs.get("m");
        if(sMaster != null){
            if(UtilsSpark.checkSparkMaster(sMaster)) {
                sparkMaster = sMaster;
            }
            else{
                sparkMaster = MACRO.TWEETER_STREAMER_DEFAULT_MASTER;
            }
            log.debug("Spark master set to: " + sparkMaster);
        }

        twitterStreamer.run(interval, sparkMaster);
        log.debug("Exit program with code (0)");
        System.exit(-0);
    }

    private void run(int interval, String sparkMaster) {

        SparkConf conf = new SparkConf().setAppName("Data Colector: Twitter Streamer [Beca Colaboracion UCM]").
                set("spark.shuffle.consolidateFiles", "false").setMaster(sparkMaster);
        init();

        JavaSparkContext sc = new JavaSparkContext(conf);

        //List names
        GeneralTransaction<Disease> diseaseGeneralTransaction = new DiseaseTransaction();
        List<Disease> diseaseList = diseaseGeneralTransaction.getAll(Disease.class);

        JavaRDD<String> diseaseNames = sc.parallelize(diseaseList)
                                                    .flatMap(processDiseaseNames).distinct()
                                                    .flatMap(addTranslations).distinct().cache();

        List<String> fSet = diseaseNames.toArray();
        diseaseGeneralTransaction.shutdown();
        sc.close();

        //Start Streaming
        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(interval));
        log.debug("Streaming context created successfully");
        setbFilters(ssc.sparkContext().broadcast(fSet.toArray(new String[fSet.size()])));
        log.debug("Broadcasts set successfully");
        JavaInputDStream<Status> tweets = TwitterUtils.createStream(ssc,
                UtilsTwitter.readTwitterCredentials("twitter4j.properties"), getbFilters());
        log.debug("Stream created successfully");

        tweets.flatMap(tweetFormatter).foreachRDD(saveFileFunction);

        log.info("Twitter streamer is about to start...");
        ssc.start();
        ssc.awaitTermination();
        log.info("Twitter streamer finished");
    }

    private void init() {


        processDiseaseNames = new FlatMapFunction<Disease,String>() {

            @Override
            public Iterable<String> call(Disease disease) throws Exception {
                Set<String> ret = new HashSet<String>();

                String diseaseName = disease.getDiseaseKey().getName();
                String[] parts = diseaseName.split(MACRO.SPACE);
                for(String part : parts){
                    ret.add(part.trim());
                }
                ret.add(diseaseName);

                return ret;
            }
        };


        addTranslations = new FlatMapFunction<String,String>(){

            @Override
            public Iterable<String> call(String s) throws Exception {
                Set<String> ret = new HashSet< String>();

                Integer[] partsLanguage = new Integer[1];
                Processor.Processor();
                Processor.detectLanguage(new String[]{s}).toArray(partsLanguage);

                if(partsLanguage != null && partsLanguage.length ==1){
                    if(partsLanguage[0] == 1){
                        String key = Translator.translate(s.trim());
                        ret.add(s);
                    }
                }
                ret.add(s);

                return ret;
            }
        };


        saveFileFunction = new Function<JavaRDD<String>, Void>(){

            public Void call(JavaRDD<String> stringJavaRDD) throws Exception {
                if(!stringJavaRDD.isEmpty()) {

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

    private static void addOptions(Options options) {
        options.addOption("t", true, "time interval for twitter streamer");
        options.addOption("m", true, "spark master");
    }
}
