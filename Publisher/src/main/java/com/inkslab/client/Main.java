package com.inkslab.client;

import com.mqSDK.PublisherUtil;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        String url = "http://localhost:8080";
        //PublisherUtil.sendMQ(url,"test","消息1");
        //PublisherUtil.sendTopic(url,"test","消息1");
        PublisherUtil.sendAll(url,"全广播消息1");
        PublisherUtil.sendAll(url,"全广播消息2");
        PublisherUtil.sendAll(url,"全广播消息3");
        PublisherUtil.sendAll(url,"全广播消息4");
        PublisherUtil.sendAll(url,"全广播消息5");
    }
}
