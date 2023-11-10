package com.example.lab1;

import com.example.lab1.middleware.MiddleWare;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
@SpringBootTest
public class MiddleWareApplicationTests3 {//点对点调度策略 发送方直接发给选择的订阅方
    private static final Logger logger = LoggerFactory.getLogger(MiddleWareApplicationTests.class);
    @Test
    void contextLoads() {
        List<String> stringList = new ArrayList<>();
        MiddleWare.appendSubcribeByPublisher("订阅者A","发布者A");
        MiddleWare.appendSubcribeByPublisher("订阅者B","发布者B");
        MiddleWare.appendSubcribeByPublisher("订阅者C","发布者A");
        MiddleWare.appendMQ("发布者A","","发布者A发布的消息情况:A支付了定金");
        MiddleWare.appendMQ("发布者A","","发布者A发布的消息情况:A取消了付款");
        MiddleWare.appendMQ("发布者B","","发布者B发布的消息情况:B修改了配送地址");
        MiddleWare.appendMQ("发布者B","","发布者B发布的消息情况:B成功下单");
        MiddleWare.appendMQ("发布者C","","发布者C发布的消息情况:C申请退款");
        MiddleWare.appendMQ("发布者D","","发布者D发布的消息情况:D交易成功");
        stringList=MiddleWare.subscribeMessageByPublisher("订阅者A");
        for (String s:stringList)
        {
            System.out.println("订阅者A"+"收到了"+s);
        }
        stringList=MiddleWare.subscribeMessageByPublisher("订阅者B");
        for (String s:stringList)
        {
            System.out.println("订阅者B"+"收到了"+s);
        }
        stringList=MiddleWare.subscribeMessageByPublisher("订阅者C");
        for (String s:stringList)
        {
            System.out.println("订阅者C"+"收到了"+s);
        }
    }
}
