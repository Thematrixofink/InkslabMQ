package com.inkslab.client;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mqSDK.PublisherUtil;

import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> param = new HashMap();
        param.put("topic", "1111");
        param.put("message", "1111");
        param.put("surTime", "1111");
        System.out.println(JSONUtil.toJsonStr(param));
        System.out.println(JSONUtil.toJsonStr("1111"));
    }
}
