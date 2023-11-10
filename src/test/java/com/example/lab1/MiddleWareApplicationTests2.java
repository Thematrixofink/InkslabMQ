package com.example.lab1;

import com.example.lab1.middleware.MiddleWare;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
@SpringBootTest
public class MiddleWareApplicationTests2 {  //全广播调度策略
    private static final Logger logger = LoggerFactory.getLogger(MiddleWareApplicationTests.class);
    @Test
    void contextLoads() {
        List<String> stringList = new ArrayList<>();
        MiddleWare.appendSubscibe("订阅者1");//添加订阅者
        MiddleWare.appendSubscibe("订阅者2");//添加订阅者
        MiddleWare.appendSubscibe("订阅者3");//添加订阅者
        MiddleWare.appendMQtoAll("发布者A","发布者A发布的第一条消息");
        MiddleWare.appendMQtoAll("发布者A","发布者A发布的第二条消息");
        MiddleWare.appendMQtoAll("发布者A","发布者A发布的第三条消息");
        MiddleWare.appendMQtoAll("发布者B","发布者B发布的第1条消息");
        MiddleWare.appendMQtoAll("发布者B","发布者B发布的第2条消息");
        stringList=MiddleWare.appendMQtoAll(null,null);//接受消息栈中发布者发布的所有信息
        for (String s:stringList)
        {
            System.out.println("订阅者1"+"收到的消息为"+s);
        }
        for (String s:stringList)
        {
            System.out.println("订阅者2"+"收到的消息为"+s);
        }
        for (String s:stringList)
        {
            System.out.println("订阅者2"+"收到的消息为"+s);
        }



    }
}
