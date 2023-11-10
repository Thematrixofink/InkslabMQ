package com.inkslab.client;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(5000);

        String url = "http://localhost:8080/publishToMQ";
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("queueId", "test");
        String msg = "消息1你收到了吗";
        param.put("message",msg);
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(param));
        System.out.println("向队列中添加消息:"+msg);
        Thread.sleep(2000);

        Map<String,Object> param1 = new HashMap<String,Object>();
        param1.put("queueId", "test");
        msg = "消息2你收到了吗";
        param1.put("message",msg);
        String response2 = HttpUtil.post(url, JSONUtil.toJsonStr(param1));
        System.out.println("向队列中添加消息:"+msg);
        Thread.sleep(2000);

        Map<String,Object> param3 = new HashMap<String,Object>();
        param3.put("queueId", "test");
        msg = "消息3你收到了吗";
        param3.put("message",msg);
        String response3 = HttpUtil.post(url, JSONUtil.toJsonStr(param3));
        System.out.println("向队列中添加消息:"+msg);
    }
}
