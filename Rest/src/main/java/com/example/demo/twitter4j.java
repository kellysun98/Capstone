package com.example.demo;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import twitter4j.*;

import twitter4j.Status;
import twitter4j.TwitterStream;


public class twitter4j {

  public static Twitter getTwitterinstance(){
      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setDebugEnabled(true)
              .setOAuthConsumerKey("6CA5tiWm9rP0iOFtPyW1UKx8Q")
              .setOAuthConsumerSecret("REMOVED")
              .setOAuthAccessToken("REMOVED")
              .setOAuthAccessTokenSecret("REMOVED");

      TwitterFactory tf = new TwitterFactory(cb.build());
      Twitter twitter = tf.getInstance();

      return twitter;

  }

    public static String createTweet(String tweet) throws TwitterException {
        Twitter twitter = getTwitterinstance();
        Status status = twitter.updateStatus("creating baeldung API");
        return status.getText();
    }

    public static List<String> getTimeLine() throws TwitterException {
        Twitter twitter = getTwitterinstance();
        List<Status> statuses = twitter.getHomeTimeline();
        return statuses.stream().map(
                item -> item.getText()).collect(
                Collectors.toList());
    }

    public static String sendDirectMessage(String recipientName, String msg) throws TwitterException {
        Twitter twitter = getTwitterinstance();
        DirectMessage message = twitter.sendDirectMessage(recipientName, msg);
        return message.getText();
    }

    public static List<String> searchtweets() throws TwitterException {
        Twitter twitter = getTwitterinstance();
        Query query = new Query("source:twitter4j baeldung");
        QueryResult result = twitter.search(query);
        List<Status> statuses = result.getTweets();
        return statuses.stream().map(
                item -> item.getText()).collect(
                Collectors.toList());
    }

    public static void streamFeed() throws InterruptedException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("6CA5tiWm9rP0iOFtPyW1UKx8Q")
                .setOAuthConsumerSecret("REMOVED")
                .setOAuthAccessToken("REMOVED")
                .setOAuthAccessTokenSecret("REMOVED");
        Configuration newcb  = cb.build();

        StatusListener listener = new StatusListener(){

            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

            @Override
            public void onScrubGeo(long l, long l1) {}

            @Override
            public void onStallWarning(StallWarning stallWarning) {}

            public void onException(Exception ex) {
                ex.printStackTrace();
            }

        };

        TwitterStream twitterStream = new TwitterStreamFactory(newcb).getInstance();

        twitterStream.addListener(listener);

        twitterStream.filter("traffic");

        TimeUnit.SECONDS.sleep(10);
        twitterStream.shutdown();

    }

    public static void main(String[] args) throws TwitterException, InterruptedException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("6CA5tiWm9rP0iOFtPyW1UKx8Q")
                .setOAuthConsumerSecret("REMOVED")
                .setOAuthAccessToken("REMOVED")
                .setOAuthAccessTokenSecret("REMOVED");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
//
        //getTimeLine();
        streamFeed();


        //streamFeed();


    }


 }