package org.example;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class Main2 {
    public static void main(String[] args) {
        registerAndListenTopic();
    }
    private static void registerAndListenMQ(){
        while(true) {
            String url = "http://localhost:8080/getMessageMQ";
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("queueId", "test");
            String response = HttpUtil.post(url, JSONUtil.toJsonStr(param));
            if(!response.contains("队列不存在") && !response.contains("队列无元素")){
                System.out.println(response);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void registerAndListenTopic(){
        //每隔1s监听队列是否发生了变化
        while(true) {
            String url = "http://localhost:8080/getMessageFromTopic";
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("consumerId", "2");
            param.put("topicId","test");
            String response = HttpUtil.post(url, JSONUtil.toJsonStr(param));
            if(!response.contains("主题不存在") && !response.contains("主题中事件为空")){
                System.out.println(response);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
