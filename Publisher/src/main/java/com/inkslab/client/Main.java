package com.inkslab.client;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //sendMQ();
        sendTopic();
    }

    private static  void sendMQ() throws InterruptedException {
        Thread.sleep(4000);

        String url = "http://localhost:8080/publishToMQ";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("queueId", "test");
        String msg = "消息1你收到了吗";
        param.put("message", msg);
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(param));
        System.out.println("向队列中添加消息:" + msg);
        Thread.sleep(2000);

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("queueId", "test");
        msg = "消息2你收到了吗";
        param1.put("message", msg);
        String response2 = HttpUtil.post(url, JSONUtil.toJsonStr(param1));
        System.out.println("向队列中添加消息:" + msg);
        Thread.sleep(2000);

        Map<String, Object> param3 = new HashMap<String, Object>();
        param3.put("queueId", "test");
        msg = "消息3你收到了吗";
        param3.put("message", msg);
        String response3 = HttpUtil.post(url, JSONUtil.toJsonStr(param3));
        System.out.println("向队列中添加消息:" + msg);
    }

    private static  void sendTopic() throws InterruptedException {
        Thread.sleep(4000);

        String url = "http://localhost:8080/publishToTopic";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("topic", "test");
        String msg = "消息1你收到了吗";
        param.put("message", msg);
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(param));
        System.out.println("向主题中添加消息:" + msg);
        Thread.sleep(2000);

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("topic", "test");
        msg = "消息2你收到了吗";
        param1.put("message", msg);
        String response2 = HttpUtil.post(url, JSONUtil.toJsonStr(param1));
        System.out.println("向主题中添加消息:" + msg);
        Thread.sleep(2000);

        Map<String, Object> param3 = new HashMap<String, Object>();
        param3.put("topic", "test");
        msg = "消息3你收到了吗";
        param3.put("message", msg);
        String response3 = HttpUtil.post(url, JSONUtil.toJsonStr(param3));
        System.out.println("向主题中添加消息:" + msg);
        Thread.sleep(2000);


        Map<String, Object> param4 = new HashMap<String, Object>();
        param4.put("topic", "test");
        msg = "消息4你收到了吗";
        param4.put("message", msg);
        String response4 = HttpUtil.post(url, JSONUtil.toJsonStr(param4));
        System.out.println("向主题中添加消息:" + msg);
        Thread.sleep(2000);


        Map<String, Object> param5 = new HashMap<String, Object>();
        param5.put("topic", "test");
        msg = "消息5你收到了吗";
        param5.put("message", msg);
        String response5 = HttpUtil.post(url, JSONUtil.toJsonStr(param5));
        System.out.println("向主题中添加消息:" + msg);
        Thread.sleep(2000);

    }
}
