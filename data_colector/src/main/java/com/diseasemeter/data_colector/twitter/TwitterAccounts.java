package com.diseasemeter.data_colector.twitter;


import com.diseasemeter.data_colector.bbdd.mongodb.GeneralOperation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoComparation;
import com.diseasemeter.data_colector.bbdd.mongodb.MongoDBController;
import com.diseasemeter.data_colector.bbdd.mysql.DiseaseTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.GeneralTransaction;
import com.diseasemeter.data_colector.bbdd.mysql.TweetTransaction;
import com.diseasemeter.data_colector.bbdd.resources.mongodb.*;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Disease;
import com.diseasemeter.data_colector.bbdd.resources.mysql.DiseaseKey;
import com.diseasemeter.data_colector.bbdd.resources.mysql.Tweet;
import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.common.UtilsTwitter;
import com.diseasemeter.data_colector.google_api.Geocoder;
import com.diseasemeter.data_colector.microsoft_api.Translator;
import com.diseasemeter.data_colector.monkey_learn.Processor;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.springframework.data.mongodb.core.query.Criteria;
import scala.Tuple2;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * It takes @CDCemergency, @CDCGlobal, @CDCespanol, @ECDC, @ECDC_Oubreaks, and @sanidadgob tweets.
 * The first three accounts corresponds to the "Centers for Disease Control and Prevention" for emergencies, global news,
 * and news in spanish; the ECDC accounts corresponds to the same centers but at European level; the last one corresponds
 * to the spanish ministry of health.
 *
 * example:
 *
 * -o data/twitter_users/ -m local[2] -u "CDCemergency, CDCGlobal, CDCespanol, ECDC, ECDC_Outbreaks, sanidadgob"
 *
 */
public class TwitterAccounts implements Serializable {

    private static Logger log = Logger.getLogger(TwitterAccounts.class);
    private static final String outputDateFormat = "dd/MM/yyyy";
    private Configuration conf;
    private List<String> diseaseList;
    //Spark
    private SparkConf sparkConf;
    //Spark Functions
    private FlatMapFunction<Disease,String> processDiseaseNames;
    private FlatMapFunction<String,String> addTranslations;
    private FlatMapFunction<Status, Tuple2<String, Tweet>> tweetFormatter;
    private FlatMapFunction<Tuple2<String, Tweet>, Void> saveFileFunction;

    public static void main(String[] args){
        BasicConfigurator.configure();
        Set<String> uList = new HashSet<String>();
        //Read input arguments
        if (args.length != 2) {
            System.out.printf("Usage: TwitterStreamer -u <commma separated list of users> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        addOptions(options);
        TwitterAccounts twitterAccounts = new TwitterAccounts();

        Map<String, String> parsedArgs = UtilsCommon.getArgs(options, args);

        String usersLine = parsedArgs.get("u");
        if(usersLine != null){
            uList = UtilsTwitter.parseLine(usersLine);
        }

        String sparkMaster = parsedArgs.get("m");
        if(sparkMaster == null){
            sparkMaster = MACRO.TWEETER_STREAMER_DEFAULT_MASTER;
        }

        twitterAccounts.setSparkConf(sparkMaster);
        twitterAccounts.setConfiguration();
        twitterAccounts.setDiseaseList();
        twitterAccounts.checkFilteredUsers(uList);
        twitterAccounts.run();
        log.debug("Program running correctly but not exiting. Code (0).");
    }

    public void setSparkConf(String sparkMaster) {
        this.sparkConf = new SparkConf().setAppName("Data Colector: Twitter Streamer [Beca Colaboracion UCM]").
                        set("spark.shuffle.consolidateFiles", "false").setMaster(sparkMaster);
    }

    private void setDiseaseList() {
        init();
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        //List names
        GeneralTransaction<Disease> diseaseGeneralTransaction = new DiseaseTransaction();
        List<Disease> diseaseList = diseaseGeneralTransaction.getAll(Disease.class);

        JavaRDD<String> diseaseNames = sc.parallelize(diseaseList)
                .flatMap(processDiseaseNames).distinct()
                .flatMap(addTranslations).distinct().cache();

        this.diseaseList = diseaseNames.toArray();
        diseaseGeneralTransaction.shutdown();
        sc.close();
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

                Set<String> filters = UtilsTwitter.getFilter(tweet.getTweetKey().getContent(), (String[]) diseaseList.toArray());
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


        saveFileFunction = new FlatMapFunction<Tuple2<String, Tweet>, Void>(){


            @Override
            public Iterable<Void> call(Tuple2<String, Tweet> stringTweetTuple2) throws Exception {
                saveTweet(stringTweetTuple2);
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
                centerOperation.insert(new Center(name, location, timestamp, new com.diseasemeter.data_colector.bbdd.resources.mongodb.Location(coordinates)));
            }
            conditions.clear();
            conditions.add(MongoDBController.createCriteria("name", MongoComparation.EQ, name));
            conditions.add(MongoDBController.createCriteria("location.coordinates", MongoComparation.EQ, coordinates));
            if(!heatpointOperation.exists(conditions, HeatPoint.class));
            heatpointOperation.insert(new HeatPoint(weight,timestamp, name, location,
                    new com.diseasemeter.data_colector.bbdd.resources.mongodb.Location(coordinates)));
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

    private void setConfiguration() {
        Properties tProp = UtilsCommon.readProperties("twitter4j.properties");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthAccessToken(tProp.getProperty("oauth.accessToken"));
        cb.setOAuthAccessTokenSecret(tProp.getProperty("oauth.accessTokenSecret"));
        cb.setOAuthConsumerKey(tProp.getProperty("oauth.consumerKey"));
        cb.setOAuthConsumerSecret(tProp.getProperty("oauth.consumerSecret"));
        cb.setUseSSL(Boolean.valueOf(tProp.getProperty("http.useSSL")));
        conf = cb.build();
    }

    private static void addOptions(Options options) {
        options.addOption("u", true, "list of comma separated users");
        options.addOption("m", true, "spark master");
    }

    private void checkFilteredUsers(Set<String> uSet) {
        TwitterFactory twitterFactory=new TwitterFactory(conf);
        Twitter twitter=twitterFactory.getInstance();

        try {
            ResponseList<Friendship> friendships = twitter.lookupFriendships(uSet.toArray(new String[uSet.size()]));

            Iterator<Friendship> it = friendships.iterator();
            while(it.hasNext()){
                Friendship friendship = it.next();
                if(friendship != null && !friendship.isFollowing()) {
                    twitter.createFriendship(friendship.getScreenName());
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        TwitterStream twitterStream = new TwitterStreamFactory(conf).getInstance();
        twitterStream.addListener(listener);
        twitterStream.addRateLimitStatusListener(rateListener);
        twitterStream.user();
    }

    private RateLimitStatusListener rateListener = new RateLimitStatusListener() {
        @Override
        public void onRateLimitStatus( RateLimitStatusEvent event ) {
            log.debug("Twitter status: Limit["+event.getRateLimitStatus().getLimit() + "], Remaining[" +event.getRateLimitStatus().getRemaining()+"]");
        }

        @Override
        public void onRateLimitReached( RateLimitStatusEvent event ) {
            log.error("Twitter status limit reached: Limit["+event.getRateLimitStatus().getLimit() + "], remaining[" +event.getRateLimitStatus().getRemaining()+"]");
        }
    };

    private UserStreamListener listener = new UserStreamListener() {

        @Override
        public void onStatus(Status status) {
            sparkConf.set("spark.driver.allowMultipleContexts","true");
            JavaSparkContext sc = new JavaSparkContext(sparkConf);
            JavaRDD<Status> tweets = sc.parallelize(Arrays.asList(new Status[]{status}));
            tweets.flatMap(tweetFormatter).flatMap(saveFileFunction).cache();

            sc.close();
        }

        @Override
        public void onException(Exception ex) {
            log.error("Exception:" + ex.getMessage());
        }
        @Override
        public void onStallWarning(StallWarning warning) {
            log.error("Got stall warning:" + warning);
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            log.error("Got a track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {}

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {}

        @Override
        public void onFriendList(long[] friendIds) {}

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {}

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {}

        @Override
        public void onFollow(User source, User followedUser) {}

        @Override
        public void onDirectMessage(DirectMessage directMessage) {}

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {}

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {}

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {}

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {}

        @Override
        public void onUserListCreation(User listOwner, UserList list) {}

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {}

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {}

        @Override
        public void onUserProfileUpdate(User updatedUser) {}

        @Override
        public void onBlock(User source, User blockedUser) {}

        @Override
        public void onUnblock(User source, User unblockedUser) {}


    };

}
