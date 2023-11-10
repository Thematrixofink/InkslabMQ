package com.example.lab1.controller;

import com.example.lab1.middleware.MiddleWare;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SubscribeController {
    @PostMapping(value = "/subscribe/topic")//处理subscribe/topic端点post请求
    String newSubscribeViaTopic(@RequestBody Map<String, String> data) {
        final String subscribe_id = data.get("sub_id");//接收数据段中的发送者id信息
        final String topic = data.get("topic");//接收数据段中的主题信息

        MiddleWare.appendSubcribeByTopic(subscribe_id, topic);

        return "OK.";
    }

    @PostMapping(value = "/subscribe/publisher")
    String newSubscribeViaPublisher(@RequestBody Map<String, String> data) {
        final String subscribe_id = data.get("sub_id");//数据段中的订阅者信息
        final String publisher_id = data.get("pub_id");//数据段中的发布者信息

        MiddleWare.appendSubcribeByPublisher(subscribe_id, publisher_id);

        return "OK.";
    }

    @PostMapping(value = "/message/topic")
    List<String> getAllMessageByTopic(@RequestBody Map<String, String> data) {
        final String subscriber_id = data.get("sub_id");
        return MiddleWare.subscribeMessageByTopic(subscriber_id);
    }

    @PostMapping(value = "/message/publisher")
    List<String> getAllMessageByPublisher(@RequestBody Map<String, String> data) {
        final String subscriber_id = data.get("sub_id");
        return MiddleWare.subscribeMessageByPublisher(subscriber_id);//获取订阅者订阅的所有发布者消息列表
    }
}
