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

}
