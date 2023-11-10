package com.example.lab1.middleware;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.common.ErrorCode;
import com.example.lab1.common.ResultUtils;
import com.example.lab1.pojo.SubList;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//提供相应的中间件方法
@Service
public class MiddleWare {
    //static final 修饰的一系列线程安全的map
    //提供一系列更新消息队列或返回消息的方法
    //储存消息发布者与其发布的主题、订阅它的订阅者的信息
    public static final Map<String, SubList> subscribeMap = new ConcurrentHashMap<>();//等价下面两个
    //public static final Map<String, List<String>> subscribeTopicMap = new ConcurrentHashMap<>();//<订阅者，List<主题>>
    //public static final Map<String,List<String>> subscribePubMap = new ConcurrentHashMap<>();//<订阅者，List<发布者>>

    //保存每个消息对应的主题和发布者都有哪个订阅者没收到
    public static final Map<Integer, Set<String>> UsefulMQ = new ConcurrentHashMap<>();

    //消息队列          Key为消息的ID   Value为消息的具体内容
    public static final Map<Integer,String> MQ = new ConcurrentHashMap<>();
    //<主题，List<>>    Key为主题     Value为与此主题相关联的消息ID
    public static final Map<String,List<Integer>> TopicMq = new ConcurrentHashMap<>();
    //<发布者，List<>>  Key为发布者的ID Value为发布者发送的消息的ID
    public static final Map<String,List<Integer>> PubMq = new ConcurrentHashMap<>();

    //消息队列  Key为队列的ID，Value相应的队列
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> msgQueues = new ConcurrentHashMap<>();

    //与发布者有关，根据发布者和主题更新TopicMq和PubMq和MQ和UsefulMQ
    /**
     * 将消息添加到消息队列
     * @param publisher 消息发布者的ID
     * @param topic     发送的主题
     * @param message   消息的具体内容
     * @return
     */
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
            //判断订阅者是否订阅了该主题或发布者
            if (subList.getSubscribeTopicSet().contains(topic)||subList.getSubscribePubSet().contains(publisher))
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


    /**
     * 向某个特定的消息队列中添加信息
     * @param queueId 队列的ID
     * @param message 发布的信息
     */
    public static BaseResponse<String> addMegToMQ(String queueId, String message) {
        //检测队列的ID合法性
        if(queueId == null ||queueId.isEmpty()) return ResultUtils.error(ErrorCode.PARAMS_ERROR,"队列的Id不能为空!");
        //如果目标队列已经存在，那么就直接向队列中添加一个消息
        if(msgQueues.containsKey(queueId)){
            ConcurrentLinkedQueue<String> msgQueue = msgQueues.get(queueId);
            msgQueue.offer(message);
            return ResultUtils.success("添加 '"+message+"' 信息到 '"+queueId+"' 队列成功");
        }
        //如果目标队列不存在，那么就返回错误，队列不存在
        else{
            ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
            messages.offer(message);
            msgQueues.put(queueId,messages);
            return ResultUtils.success("添加 '"+message+"' 信息到 '"+queueId+"' 队列成功");
            //return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR,"目标队列不存在");
        }
    }

    /**
     * 从某个特定的消息队列中发送信息
     * @param queueId 队列的名称
     * @return 返回响应
     */
    public static BaseResponse<String> getMsgFromMQ(String queueId) {
        if(!msgQueues.containsKey(queueId)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"队列不存在");
        }else{
            ConcurrentLinkedQueue<String> msg = msgQueues.get(queueId);
            if(msg.isEmpty()){
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR,"队列无元素");
            }else{
                return ResultUtils.success(msg.poll());
            }
        }
    }
}
