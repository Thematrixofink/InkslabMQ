package com.example.lab1.controller;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.middleware.MiddleWare;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 消息生产者
 */
@RestController
public class PublisherController {


    /**
     * 全广播模式
     * @param message 发送的数据
     * @return 返回响应结果
     */
    @PostMapping("/publishAll")
    public BaseResponse<String> publishMsgAll(@RequestParam String message){
        System.out.println(message);
        return MiddleWare.addMsg(message);
    }

    /**
     * 选择广播式-点对点
     * 基于消息队列
     * 发布者向消息队列里面发送一个事件
     * @param data 发送的数据
     * @return
     */
    @PostMapping("/publishToMQ")
    public BaseResponse<String> publishMsgToMQ(@RequestBody Map<String, String> data){
        //从data中提取相应相应字段的数据 此为队列的ID
        final String pub_id = data.get("queueId");
        //发送的字段
        final String message = data.get("message");
        return MiddleWare.addMegToMQ(pub_id,message);
    }

    /**
     * 选择广播式-发布-订阅模式
     * @param data
     * @return
     */
    @PostMapping(value = "/publishToTopic")
    public BaseResponse<String> publishMsgToTopic(@RequestBody Map<String, String> data) {
        final String topic = data.get("topic");//发布的主题
        final String message = data.get("message");//发送的字段
        final String surTime = data.get("surTime");
        return MiddleWare.appendToTopic(topic, message,Long.parseLong(surTime));//添加,到消息队列里
    }

}
