package com.diseasemeter.data_colector.twitter;


import com.diseasemeter.data_colector.common.MACRO;
import com.diseasemeter.data_colector.common.UtilsCommon;
import com.diseasemeter.data_colector.common.UtilsFS;
import com.diseasemeter.data_colector.common.UtilsTwitter;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
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
    private Configuration conf;
    //private HiveJdbcClient hiveClient;
    //private GenericMapper tweetMapper;

    public static void main(String[] args){
        BasicConfigurator.configure();
        String outpurDir = "";
        Set<String> uList = new HashSet<String>();
        //Read input arguments
        if (args.length != 4) {
            System.out.printf("Usage: TwitterStreamer -o <output dir> -u <commma separated list of users> \n");
            log.error("Exit program with code (-1). Insufficient calling arguments");
            System.exit(-1);
        }
        // create Options object
        Options options = new Options();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        addOptions(options);
        TwitterAccounts twitterAccounts = new TwitterAccounts();
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
            if(cmd.hasOption("u")) {
                String usersLine = cmd.getOptionValue("u");
                if(usersLine != null){
                    uList = UtilsTwitter.parseLine(usersLine);
                }
            }
        }

        twitterAccounts.setConfiguration();
        twitterAccounts.setHiveClient();
        twitterAccounts.setTweetMapper();
        twitterAccounts.checkFilteredUsers(uList);
        twitterAccounts.run();
        log.debug("Program running correctly but not exiting. Code (0).");
    }

    private void setTweetMapper() {
        //tweetMapper = new TweetMapper(hiveClient, BBDDMACRO.TWITTER_USERS_TABLE_NAME, BBDDMACRO.TWITTER_USERS_TABLE_COLUMNS);
    }

    private void setHiveClient() {
        /*hiveClient = new HiveJdbcClient(MACRO.BBDD_PROPERTIES_FILE);
        if(!hiveClient.isConnected()){
            if(!hiveClient.connect()){

            }
        }*/
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
        options.addOption("o", true, "output directory");
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
            Tweet tweet = new Tweet(status.getCreatedAt(),status.getPlace(), status.getText(), status.getUser());
            log.debug("Tweet from "+tweet.getUserName()+" processed");
            //Needs disease and status to be correct
            //tweetMapper.insert(tweet);
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
