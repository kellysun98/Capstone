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

public class TwitterService {

    public void GetTwitter(boolean receiving){

    }
    public static void main(String[] args) throws TwitterException {


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("6CA5tiWm9rP0iOFtPyW1UKx8Q")
                .setOAuthConsumerSecret("HqfTFHGry2rSHbY25lJlsnNtzKl389PggyQmPCdylYSafRjkAq")
                .setOAuthAccessToken("1687345548-04LLvQPqFlrL3Yjm2evlDIgmyolNxTkWfoU0VwD")
                .setOAuthAccessTokenSecret("ddNyQmpCmpjsZqj98XhS3Voash6LKoEC7SCOivnfePc66");
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
                .getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.filter("traffic");
        twitterStream.sample();

    }
}
