package com.example.lab1.controller;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.middleware.MiddleWare;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 消息生产者
 */
@RestController
public class PublisherController {  //处理HTTP请求并返回响应

    /**
     * 点对点模式，基于消息队列
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

    @PostMapping(value = "/publish")
    public String newPublish(@RequestBody Map<String, String> data) {
        final String pub_id = data.get("pub_id");//从data中提取相应相应字段的数据 此为发布者id
        final String topic = data.get("topic");//发布的主题
        final String message = data.get("message");//发送的字段

        MiddleWare.appendMQ(pub_id, topic, message);//添加到消息队列里

        return "OK.";
    }
}
//此部分是使用Spring框架编写的控制器类
