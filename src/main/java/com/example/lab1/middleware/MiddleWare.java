package com.example.lab1.middleware;

import com.example.lab1.pojo.SubList;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//提供相应的中间件方法
@Service
public class MiddleWare {
    //static final 修饰的一系列线程安全的map
    //提供一系列更新消息队列或返回消息的方法
    public static final Map<String, SubList> subscribeMap = new ConcurrentHashMap<>();//等价下面两个
    //public static final Map<String, List<String>> subscribeTopicMap = new ConcurrentHashMap<>();//<订阅者，List<主题>>
    //public static final Map<String,List<String>> subscribePubMap = new ConcurrentHashMap<>();//<订阅者，List<发布者>>
    public static final Map<Integer, Set<String>> UsefulMQ = new ConcurrentHashMap<>();//保存每个消息对应的主题和发布者都有哪个订阅者没收到
    public static final Map<Integer,String> MQ = new ConcurrentHashMap<>();//消息队列
    public static final Map<String,List<Integer>> TopicMq = new ConcurrentHashMap<>();//<主题，List<>>
    public static final Map<String,List<Integer>> PubMq = new ConcurrentHashMap<>();//<发布者，List<>>
    public static final List<String> stringList1=new ArrayList<>();

    //与发布者有关，根据发布者和主题更新TopicMq和PubMq和MQ和UsefulMQ
    public static List<String> appendMQ(String publisher,String topic,String message)
    {
        int size= MQ.size();
        MQ.put(size+1,message);//更新消息队列，将新的消息添加进消息栈
        if (PubMq.containsKey(publisher))//查看发布者列表是否包含指定的发布者
        {
            List<Integer> list=PubMq.get(publisher);
            list.add(size+1);
        }
        else
        {
            List<Integer> l=new ArrayList<>();
            l.add(size+1);
            PubMq.put(publisher,l);//添加新的发布者和消息序号的对应集合
        }
        if (TopicMq.containsKey(topic))
        {
            List<Integer> list = TopicMq.get(topic);
            list.add(size+1);
        }
        else
        {
            List<Integer> l=new ArrayList<>();
            l.add(size+1);
            TopicMq.put(topic,l);//添加新的主题和消息序号的对应集合
        }
        for (String sub:subscribeMap.keySet())//遍历每个订阅者
        {
            SubList subList=subscribeMap.get(sub);//获取每个订阅者的订阅消息
            if (subList.getSubscribeTopicSet().contains(topic)||subList.getSubscribePubSet().contains(publisher))//判断订阅者是否订阅了该主题或发布者
            {
               if (UsefulMQ.containsKey(size+1)) //添加后续订阅者（如果有的话）
                {
                    Set<String> set = new HashSet<>();
                    set = UsefulMQ.get(size+1);
                    set.add(sub);
                }
                else //添加第一个订阅者
                {
                    Set<String> set = new HashSet<>();
                    set.add(sub);
                    UsefulMQ.put(size+1,set);
                }
            }
        }
        List<String> stringList=new ArrayList<>();
        return stringList;
    }
    //与订阅者有关，根据主题更新subscribeMap
    public static List<String> appendSubcribeByTopic(String subscriber,String topic)
    {
        if (subscribeMap.containsKey(subscriber))
        {
            SubList subList = subscribeMap.get(subscriber);
            subList.subscribeTopicSet.add(topic);//对订阅者添加主题
        }
        else
        {//添加订阅者并添加主题
            SubList subList=new SubList();
            Set<String> pubSet=new HashSet<>();
            Set<String> topicSet=new HashSet<>();
            topicSet.add(topic);
            subList.setSubscribePubSet(pubSet);
            subList.setSubscribeTopicSet(topicSet);
            subscribeMap.put(subscriber,subList);
        }
        List<String> stringList = new ArrayList<>();
        return stringList;
    }
    //与订阅者有关，根据发布者更新subscribeMap
    public static List<String> appendSubcribeByPublisher(String subscriber,String publisher)
    {
        if (subscribeMap.containsKey(subscriber))
        {
            SubList subList = subscribeMap.get(subscriber);
            subList.subscribePubSet.add(publisher);
        }
        else
        {
            SubList subList=new SubList();
            Set<String> pubSet=new HashSet<>();
            Set<String> topicSet=new HashSet<>();
            pubSet.add(publisher);
            subList.setSubscribePubSet(pubSet);
            subList.setSubscribeTopicSet(topicSet);
            subscribeMap.put(subscriber,subList);
        }
        List<String> stringList = new ArrayList<>();
        return stringList;
    }

    //与订阅者有关，根据主题返回消息队列
    public static List<String> subscribeMessageByTopic(String subcriber)
    {
        SubList subList = subscribeMap.get(subcriber);//得到订阅者所有订阅的主题
        Set<String> topicSet = subList.getSubscribeTopicSet();
        List<String> stringList=new ArrayList<>();
        for (String topic:topicSet)
        {
            List<Integer> list=new ArrayList<>();
            list=TopicMq.get(topic);
            List<Integer> newlist1=new ArrayList<>();
            newlist1.addAll(list);
            for (Integer i:newlist1)
            {
                if (!MQ.containsKey(i))
                {
                    list.remove(i);//数据筛选，去除不在消息栈中的数据
                }
            }
            List<Integer> newlist=new ArrayList<>();
            newlist.addAll(list);
            for (Integer i:newlist)
            {
                Set<String> stringSet=new HashSet<>();
                stringSet=UsefulMQ.get(i);
                if(stringSet.contains(subcriber))
                {
                    stringSet.remove(subcriber);
                    stringList.add(MQ.get(i));
                }
                if (stringSet.isEmpty())
                {
                    UsefulMQ.remove(i);
                    MQ.remove(i);
                    list.remove(i);
                }
            }
        }
       return stringList;
    }

    //与订阅者有关，根据订阅者返回消息队列
    public static List<String> subscribeMessageByPublisher(String subcriber)
    {
        SubList subList = subscribeMap.get(subcriber);
        Set<String> pubSet = subList.getSubscribePubSet();
        List<String> stringList=new ArrayList<>();
        for (String pub:pubSet)
        {
            List<Integer> list=new ArrayList<>();
            list=PubMq.get(pub);
            List<Integer> newlist1=new ArrayList<>();
            newlist1.addAll(list);
            for (Integer i:newlist1)
            {
                if (!MQ.containsKey(i))
                {
                    list.remove(i);
                }
            }
            List<Integer> newlist=new ArrayList<>();
            newlist.addAll(list);
            for (Integer i:newlist)
            {
                Set<String> stringSet=new HashSet<>();
                stringSet=UsefulMQ.get(i);
                if (stringSet.contains(subcriber))
                {
                    stringSet.remove(subcriber);
                    stringList.add(MQ.get(i));
                }
                if (stringSet.isEmpty())
                {
                    UsefulMQ.remove(i);
                    MQ.remove(i);
                    list.remove(i);
                }
            }
        }
        return stringList;
    }
    public static List<String> appendMQtoAll(String publisher,String message){
        if(publisher!=null) {
            int size = MQ.size();
            MQ.put(size + 1, message);
            stringList1.add(MQ.get(size + 1));
            return stringList1;
        }
        else{
            return stringList1;
        }
    }
    public static List<String> appendSubscibe(String subscriber){
        if (subscribeMap.containsKey(subscriber))
        {
            SubList subList = subscribeMap.get(subscriber);
        }
        else
        {
            SubList subList=new SubList();
            Set<String> pubSet=new HashSet<>();
            Set<String> topicSet=new HashSet<>();
            subList.setSubscribePubSet(pubSet);
            subList.setSubscribeTopicSet(topicSet);
            subscribeMap.put(subscriber,subList);
        }
        List<String> stringList = new ArrayList<>();
        return stringList;
    }
}
