package com.diseasemeter.data_colector.twitter;


import com.diseasemeter.data_colector.bbdd.mongodb.GeneralOperation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoComparation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoDBController;
import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.TweetTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Center;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.HeatPoint;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.Location;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.bbdd.resources.mysql.DiseaseKey;
import com.diseasemeter.data_colector.bbdd.resources.mysql.News;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Tweet;
import com.diseasemeter.data_colector.common.*;
import com.diseasemeter.data_colector.google_api.Geocoder;
import com.diseasemeter.data_colector.microsoft_api.Translator;
import com.diseasemeter.data_colector.monkey_learn.Processor;
import com.monkeylearn.Tuple;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.springframework.data.mongodb.core.query.Criteria;
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
    private static final String outputDateFormat = "dd/MM/yyyy";
    private Broadcast<String[]> bFilters;

    //Spark functions
    private FlatMapFunction<Disease,String> processDiseaseNames;
    private FlatMapFunction<String,String> addTranslations;
    private FlatMapFunction<Status, Tuple2<String, Tweet>> tweetFormatter;
    private Function<JavaRDD<Tuple2<String, Tweet>>, Void> saveFileFunction;

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
                    if(!part.toLowerCase().equals("yellow")
                            && !part.toLowerCase().equals("fever")
                            && !part.toLowerCase().equals("virus"))
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
                        ret.add(key);
                    }
                }
                ret.add(s);

                return ret;
            }
        };


        tweetFormatter = new FlatMapFunction<Status, Tuple2<String, Tweet>>() {
            @Override
            public Iterable<Tuple2<String, Tweet>> call(Status status) throws Exception {
                Set<Tuple2<String, Tweet>> ret = new HashSet<Tuple2<String, Tweet>>();
                Tweet tweet = new Tweet(status.getCreatedAt(),status.getPlace(), status.getText(), status.getUser());

                Set<String> filters = UtilsTwitter.getFilter(tweet.getTweetKey().getContent(), getbFilters());
                if(!filters.isEmpty()){
                    Processor.Processor();
                    Integer[] partsLanguage = new Integer[1];
                    Processor.detectLanguage(new String[]{tweet.getTweetKey().getContent()}).toArray(partsLanguage);

                    if(partsLanguage != null && partsLanguage.length ==1) {
                        Set<String> locations = Processor.locationExtractor(partsLanguage[0], new String[]{tweet.getTweetKey().getContent()});
                        if (locations != null && locations.size() > 0) {
                            for (String location : locations) {
                                for (String filter : filters) {
                                    tweet.setCreationPlaceName(location);
                                    ret.add(new Tuple2<String, Tweet>(filter, tweet));
                                }
                            }
                        }
                    }
                    for (String filter : filters) {
                        ret.add(new Tuple2<String, Tweet>(filter, tweet));
                    }
                }
                log.debug("Tweet processed with " + filters.size() + " filters");
                return ret;
            }
        };


        saveFileFunction = new Function<JavaRDD<Tuple2<String, Tweet>>, Void>(){


            @Override
            public Void call(JavaRDD<Tuple2<String, Tweet>> tuple2JavaRDD) throws Exception {

                if(!tuple2JavaRDD.isEmpty()) {
                    Iterator<Tuple2<String, Tweet>> it = tuple2JavaRDD.toLocalIterator();
                    while(it.hasNext()){
                        saveTweet(it.next());
                    }
                }
                return null;
            }

        };
    }

    private static void saveTweet(Tuple2<String, Tweet> stringTweetTuple2) {
        String key = stringTweetTuple2._1();
        Tweet tweet = stringTweetTuple2._2();

        GeneralTransaction<Disease> diseaseTransaction = new DiseaseTransaction();
        GeneralTransaction<Tweet> tweetTransaction = new TweetTransaction();

        if(processTweet(tweet) == 2){
            key = Translator.translate(key.trim());
        }

        List<String> diseaseLikeList = ((DiseaseTransaction)diseaseTransaction).getAllByNameLike(
                new Disease(new DiseaseKey(key, null)));

        if(tweet.noLocation()){
            for(String sDisease : diseaseLikeList) {
                List<Disease> diseaseListByName = ((DiseaseTransaction)diseaseTransaction)
                                                    .getAllByName(new Disease(new DiseaseKey(sDisease, null)));
                for(Disease dByName : diseaseListByName) {
                    processDisease(tweet, diseaseTransaction, dByName);
                    if (!tweetTransaction.exists(tweet)) {
                        tweet.getTweetKey().setDisease(dByName.getDiseaseKey().getName());
                        tweetTransaction.insert(tweet);
                    }
                    insertIntoMongo(dByName.getDiseaseKey().getLocation(), dByName.getDiseaseKey().getName(),
                                    tweet.getWeight(), tweet.getCreationTime());
                }
            }
        }
        else{
            String location = tweet.getPreferredLocation();
            for(String sDisease : diseaseLikeList) {
                Disease disease = new Disease(new DiseaseKey(sDisease, location));
                processDisease(tweet, diseaseTransaction, disease);
                if(!tweetTransaction.exists(tweet)){
                    tweet.getTweetKey().setDisease(disease.getDiseaseKey().getName());
                    tweetTransaction.insert(tweet);
                }
                insertIntoMongo(disease.getDiseaseKey().getLocation(), disease.getDiseaseKey().getName(),
                                tweet.getWeight(), tweet.getCreationTime());
            }
        }

        diseaseTransaction.shutdown();
        tweetTransaction.shutdown();
    }

    private static void insertIntoMongo(String location, String name, int weight, String date) {
        GeneralOperation<Center> centerOperation = new GeneralOperation<Center>();
        GeneralOperation<HeatPoint> heatpointOperation = new GeneralOperation<HeatPoint>();

        Double[] coordinates = Geocoder.geocode(location);

        Set<Criteria> conditions = new HashSet<Criteria>();
        conditions.add(MongoDBController.createCriteria("name", MongoComparation.EQ, name));
        if(coordinates != null) {
            long timestamp = UtilsCommon.getTimestampFromDate(outputDateFormat, date);
            if(!centerOperation.existsAtMaxDistance(Center.class, "location", coordinates, 500, conditions)) {
                centerOperation.insert(new Center(name, location, timestamp, new Location(coordinates)));
            }
            conditions.clear();
            conditions.add(MongoDBController.createCriteria("name", MongoComparation.EQ, name));
            conditions.add(MongoDBController.createCriteria("location.coordinates", MongoComparation.EQ, coordinates));
            if(!heatpointOperation.exists(conditions, HeatPoint.class));
            heatpointOperation.insert(new HeatPoint(weight,timestamp, name, location,
                    new Location(coordinates)));
        }
    }

    private static int processTweet(Tweet tweet) {
        int ret = 2;
        String[] textList = new String[]{tweet.getTweetKey().getContent()};
        Processor.Processor();
        List<Integer> languageDetections = Processor.detectLanguage(textList);
        if(languageDetections != null && languageDetections.size() == 1){
            List<Integer> sentimentDetections = null;
            if(languageDetections.get(0) == 1){
                ret = 1;
                sentimentDetections = Processor.sentimentAnalysis(1, textList);
            }
            else{
                sentimentDetections = Processor.sentimentAnalysis(2, textList);
            }
            if(sentimentDetections != null && sentimentDetections.size() == 1){
                tweet.setLanguage(languageDetections.get(0));
                tweet.setSentiment(sentimentDetections.get(0));
                tweet.setWeight(Tweet.getWeightFromSentiment(sentimentDetections.get(0)));
            }
        }
        return ret;
    }

    private static void processDisease(Tweet tweet, GeneralTransaction<Disease> diseaseTransaction, Disease disease) {
        if (!diseaseTransaction.exists(disease)) {
            disease.setWeight(disease.getWeight() + tweet.getWeight());
            disease.setLevel(Disease.getLevelFromNewWeight(disease.getWeight()));
            disease.setTweetsCount(disease.getTweetsCount()+1);
            diseaseTransaction.insert(disease);
        }
        else{
            Disease dAux = diseaseTransaction.findByKey(disease);
            dAux.setTweetsCount(dAux.getTweetsCount()+1);
            dAux.setLastUpdate(UtilsCommon.getCurrentDate(outputDateFormat));
            dAux.setWeight(dAux.getWeight()+tweet.getWeight());
            dAux.setLevel(Disease.getLevelFromNewWeight(dAux.getWeight()));
            diseaseTransaction.update(dAux);
        }
    }

    private static void addOptions(Options options) {
        options.addOption("t", true, "time interval for twitter streamer");
        options.addOption("m", true, "spark master");
    }
}
