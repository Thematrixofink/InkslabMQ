package com.example.lab1;

import com.example.lab1.middleware.MiddleWare;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MiddleWareApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(MiddleWareApplicationTests.class);

    @Test
    void contextLoads() {
        List<String> stringList = new ArrayList<>();
        MiddleWare.appendSubcribeByTopic("订阅者A","话题1");//建立订阅关系
        MiddleWare.appendSubcribeByTopic("订阅者A","话题2");
        MiddleWare.appendSubcribeByPublisher("观察者A","被观察者A");
        MiddleWare.appendSubcribeByTopic("订阅者B","话题2");
        MiddleWare.appendSubcribeByPublisher("观察者B","被观察者B");
        MiddleWare.appendSubcribeByTopic("订阅者C","话题1");
        MiddleWare.appendSubcribeByPublisher("观察者C","被观察者A");
        MiddleWare.appendMQ("发布者A","话题1","学者A在话题1发布的消息");//建立发布者与主题联系
        MiddleWare.appendMQ("发布者A","话题2","学者A在话题2发布的消息");
        MiddleWare.appendMQ("发布者B","话题1","学者B在话题1发布的消息");
        MiddleWare.appendMQ("发布者B","话题2","学者B在话题2发布的消息");
        MiddleWare.appendMQ("发布者B","话题3","学者B在话题3发布的消息");
        MiddleWare.appendMQ("发布者C","话题1","学者C在话题1发布的消息");
      //  MiddleWare.appendMQ("被观察者A","","被观察者A发布的消息情况");
      //  MiddleWare.appendMQ("被观察者B","","被观察者B发布的消息情况");
      //  MiddleWare.appendMQ("被观察者C","","被观察者C发布的消息情况");
      //  MiddleWare.appendMQ("被观察者D","","被观察者D发布的消息情况");


        //stringList=MiddleWare.subscribeMessageByTopic("");

      /*  stringList=MiddleWare.subscribeMessageByPublisher("观察者A");
        for (String s:stringList)
        {
            System.out.println("观察者A"+"收到了"+s);
        }  */
        stringList=MiddleWare.subscribeMessageByTopic("订阅者A");
        for (String s:stringList)
        {
            System.out.println("订阅者A"+"收到了"+s);
        }
        /*
        stringList=MiddleWare.subscribeMessageByPublisher("观察者B");
        for (String s:stringList)
        {
            System.out.println("观察者B"+"收到了"+s);
        } */
        stringList=MiddleWare.subscribeMessageByTopic("订阅者B");
        for (String s:stringList)
        {
            System.out.println("订阅者B"+"收到了"+s);
        }
        /*
        stringList=MiddleWare.subscribeMessageByPublisher("观察者C");
        for (String s:stringList)
        {
            System.out.println("观察者C"+"收到了"+s);
        }
         */
        stringList=MiddleWare.subscribeMessageByTopic("订阅者C");
        for (String s:stringList)
        {
            System.out.println("订阅者C"+"收到了"+s);
        }
        //MiddleWare.subscribeMessageByPublisher("");
    }

}
