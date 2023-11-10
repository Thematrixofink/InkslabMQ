package com.example.lab1.controller;

import com.example.lab1.middleware.MiddleWare;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PublisherController {  //处理HTTP请求并返回响应
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
