package com.example.lab1.controller;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.middleware.MiddleWare;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 消息消费者（观察者）
 */
@RestController
public class SubscribeController {
    /**
     * 注册模块到消息队列
     * @param moduleId 模块的id
     * @return 返回响应的结果
     */
    @PostMapping("/addModule")
    public BaseResponse<String> addModule(@RequestBody String moduleId){
        return MiddleWare.addModule(moduleId);
    }
    /**
     * 向某个队列注册，并开始监听队列，接收消息
     * @return 返回监听到的信息
     */
    @PostMapping("/getMessageFromMQ")
    public BaseResponse<String> getMessageFromMQ(@RequestBody Map<String, String> data){
        final String queueId = data.get("queueId");
        return MiddleWare.getMsgFromMQ(queueId);
    }
    /**
     * 向某个队列注册，并开始监听队列，接收消息
     * @return 返回监听到的信息
     */
    @PostMapping("/getMessageFromTopic")
    public BaseResponse<String> getMessageFromTopic(@RequestBody Map<String, String> data){
        final String consumerId = data.get("consumerId");
        final String topicId = data.get("topicId");
        return MiddleWare.getMsgFromTopic(consumerId,topicId);
    }

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
