package com.example.demo.Services;

import com.example.demo.DemoApplication;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class twitter {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    HashMap<String, String> Tweets  = new HashMap<String, String>();
    public twitter(){}

    public HashMap<String, String> streamFeed() throws InterruptedException {

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("6CA5tiWm9rP0iOFtPyW1UKx8Q")
                .setOAuthConsumerSecret("HqfTFHGry2rSHbY25lJlsnNtzKl389PggyQmPCdylYSafRjkAq")
                .setOAuthAccessToken("1687345548-04LLvQPqFlrL3Yjm2evlDIgmyolNxTkWfoU0VwD")
                .setOAuthAccessTokenSecret("ddNyQmpCmpjsZqj98XhS3Voash6LKoEC7SCOivnfePc66");
        Configuration newcb  = cb.build();

        StatusListener listener = new StatusListener(){

            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
                Tweets.put(status.getUser().getName(),status.getText());
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

        FilterQuery fq = new FilterQuery();
        fq.follow(1687345548);
        //add keywords
        twitterStream.filter(fq);

//        TimeUnit.SECONDS.sleep(10);
//        twitterStream.shutdown();

        return Tweets;
    }

}
