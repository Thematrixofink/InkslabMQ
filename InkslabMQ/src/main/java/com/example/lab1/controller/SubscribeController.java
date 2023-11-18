package com.example.lab1.controller;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.middleware.MiddleWare;
import com.example.lab1.middleware.MiddleWareImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 消息消费者（观察者）
 */
@RestController
public class SubscribeController {

    private final MiddleWare middleWare;

    {
        middleWare = new MiddleWareImpl();
    }

    /**
     * 注册模块到消息队列
     * @param moduleId 模块的id
     * @return 返回响应的结果
     */
    @PostMapping("/addModule")
    public BaseResponse<String> addModule(@RequestBody String moduleId){
        //MiddleWare middleWare = new MiddleWareImpl();
        return middleWare.addModule(moduleId);
    }
    /**
     * 向某个队列注册，并开始监听队列，接收消息
     * @return 返回监听到的信息
     */
    @PostMapping("/getMessageFromMQ")
    public BaseResponse<String> getMessageFromMQ(@RequestBody Map<String, String> data){
        //MiddleWare middleWare = new MiddleWareImpl();
        final String queueId = data.get("queueId");
        return middleWare.getMsgFromMQ(queueId);
    }
    /**
     * 向某个队列注册，并开始监听队列，接收消息
     * @return 返回监听到的信息
     */
    @PostMapping("/getMessageFromTopic")
    public BaseResponse<String> getMessageFromTopic(@RequestBody Map<String, String> data){
        //MiddleWare middleWare = new MiddleWareImpl();
        final String consumerId = data.get("consumerId");
        final String topicId = data.get("topicId");
        return middleWare.getMsgFromTopic(consumerId,topicId);
    }

}
