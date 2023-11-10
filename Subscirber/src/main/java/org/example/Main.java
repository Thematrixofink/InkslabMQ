package org.example;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //每隔1s监听队列是否发生了变化
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
}
