package org.example;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mqSDK.SubscirberUtil;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
       String url = "http://localhost:8080";
       SubscirberUtil.addModule(url, "1");
    }
}
