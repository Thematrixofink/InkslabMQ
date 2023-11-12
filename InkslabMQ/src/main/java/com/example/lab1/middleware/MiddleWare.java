package com.example.lab1.middleware;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.common.ErrorCode;
import com.example.lab1.common.ResultUtils;
import com.example.lab1.pojo.SubList;


import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//提供相应的中间件方法
@Service
@Slf4j
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
    public static final Map<Integer, String> MQ = new ConcurrentHashMap<>();
    //<主题，List<>>    Key为主题     Value为与此主题相关联的消息ID
    public static final Map<String, List<Integer>> TopicMq = new ConcurrentHashMap<>();
    //<发布者，List<>>  Key为发布者的ID Value为发布者发送的消息的ID
    public static final Map<String, List<Integer>> PubMq = new ConcurrentHashMap<>();


    //消息
    private static final ConcurrentHashMap<String, Vector<String>> msg = new ConcurrentHashMap<>();

    //消息队列  Key为队列的ID，Value相应的队列
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> msgQueues = new ConcurrentHashMap<>();
    //Topic
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topics = new ConcurrentHashMap<>();
    //保存topic中哪个消息没有被收到，Key为topic，Value为Map<订阅者Id,需要发送给订阅者的消息>
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Vector<String>>> toSendUser = new ConcurrentHashMap<>();
    //保存订阅响应主题的用户,Key 为topic， Value为
    private static final ConcurrentHashMap<String, Vector<String>> topicUser = new ConcurrentHashMap<>();

    //与发布者有关，根据发布者和主题更新TopicMq和PubMq和MQ和UsefulMQ

    /**
     * 将消息添加到消息队列
     *
     * @param publisher 消息发布者的ID
     * @param topic     发送的主题
     * @param message   消息的具体内容
     * @return
     */
    public static List<String> appendMQ(String publisher, String topic, String message) {
        int size = MQ.size();
        MQ.put(size + 1, message);//更新消息队列，将新的消息添加进消息栈
        if (PubMq.containsKey(publisher))//查看发布者列表是否包含指定的发布者
        {
            List<Integer> list = PubMq.get(publisher);
            list.add(size + 1);
        } else {
            List<Integer> l = new ArrayList<>();
            l.add(size + 1);
            PubMq.put(publisher, l);//添加新的发布者和消息序号的对应集合
        }
        if (TopicMq.containsKey(topic)) {
            List<Integer> list = TopicMq.get(topic);
            list.add(size + 1);
        } else {
            List<Integer> l = new ArrayList<>();
            l.add(size + 1);
            TopicMq.put(topic, l);//添加新的主题和消息序号的对应集合
        }
        for (String sub : subscribeMap.keySet())//遍历每个订阅者
        {
            SubList subList = subscribeMap.get(sub);//获取每个订阅者的订阅消息
            //判断订阅者是否订阅了该主题或发布者
            if (subList.getSubscribeTopicSet().contains(topic) || subList.getSubscribePubSet().contains(publisher)) {
                if (UsefulMQ.containsKey(size + 1)) //添加后续订阅者（如果有的话）
                {
                    Set<String> set = new HashSet<>();
                    set = UsefulMQ.get(size + 1);
                    set.add(sub);
                } else //添加第一个订阅者
                {
                    Set<String> set = new HashSet<>();
                    set.add(sub);
                    UsefulMQ.put(size + 1, set);
                }
            }
        }
        List<String> stringList = new ArrayList<>();
        return stringList;
    }

    //与订阅者有关，根据主题更新subscribeMap
    public static List<String> appendSubcribeByTopic(String subscriber, String topic) {
        if (subscribeMap.containsKey(subscriber)) {
            SubList subList = subscribeMap.get(subscriber);
            subList.subscribeTopicSet.add(topic);//对订阅者添加主题
        } else {//添加订阅者并添加主题
            SubList subList = new SubList();
            Set<String> pubSet = new HashSet<>();
            Set<String> topicSet = new HashSet<>();
            topicSet.add(topic);
            subList.setSubscribePubSet(pubSet);
            subList.setSubscribeTopicSet(topicSet);
            subscribeMap.put(subscriber, subList);
        }
        List<String> stringList = new ArrayList<>();
        return stringList;
    }

    //与订阅者有关，根据发布者更新subscribeMap
    public static List<String> appendSubcribeByPublisher(String subscriber, String publisher) {
        if (subscribeMap.containsKey(subscriber)) {
            SubList subList = subscribeMap.get(subscriber);
            subList.subscribePubSet.add(publisher);
        } else {
            SubList subList = new SubList();
            Set<String> pubSet = new HashSet<>();
            Set<String> topicSet = new HashSet<>();
            pubSet.add(publisher);
            subList.setSubscribePubSet(pubSet);
            subList.setSubscribeTopicSet(topicSet);
            subscribeMap.put(subscriber, subList);
        }
        List<String> stringList = new ArrayList<>();
        return stringList;
    }

    //与订阅者有关，根据主题返回消息队列
    public static List<String> subscribeMessageByTopic(String subcriber) {
        SubList subList = subscribeMap.get(subcriber);//得到订阅者所有订阅的主题
        Set<String> topicSet = subList.getSubscribeTopicSet();
        List<String> stringList = new ArrayList<>();
        for (String topic : topicSet) {
            List<Integer> list = new ArrayList<>();
            list = TopicMq.get(topic);
            List<Integer> newlist1 = new ArrayList<>();
            newlist1.addAll(list);
            for (Integer i : newlist1) {
                if (!MQ.containsKey(i)) {
                    list.remove(i);//数据筛选，去除不在消息栈中的数据
                }
            }
            List<Integer> newlist = new ArrayList<>();
            newlist.addAll(list);
            for (Integer i : newlist) {
                Set<String> stringSet = new HashSet<>();
                stringSet = UsefulMQ.get(i);
                if (stringSet.contains(subcriber)) {
                    stringSet.remove(subcriber);
                    stringList.add(MQ.get(i));
                }
                if (stringSet.isEmpty()) {
                    UsefulMQ.remove(i);
                    MQ.remove(i);
                    list.remove(i);
                }
            }
        }
        return stringList;
    }

    //与订阅者有关，根据订阅者返回消息队列
    public static List<String> subscribeMessageByPublisher(String subcriber) {
        SubList subList = subscribeMap.get(subcriber);
        Set<String> pubSet = subList.getSubscribePubSet();
        List<String> stringList = new ArrayList<>();
        for (String pub : pubSet) {
            List<Integer> list = new ArrayList<>();
            list = PubMq.get(pub);
            List<Integer> newlist1 = new ArrayList<>();
            newlist1.addAll(list);
            for (Integer i : newlist1) {
                if (!MQ.containsKey(i)) {
                    list.remove(i);
                }
            }
            List<Integer> newlist = new ArrayList<>();
            newlist.addAll(list);
            for (Integer i : newlist) {
                Set<String> stringSet = new HashSet<>();
                stringSet = UsefulMQ.get(i);
                if (stringSet.contains(subcriber)) {
                    stringSet.remove(subcriber);
                    stringList.add(MQ.get(i));
                }
                if (stringSet.isEmpty()) {
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
     *
     * @param queueId 队列的ID
     * @param message 发布的信息
     */
    public static BaseResponse<String> addMegToMQ(String queueId, String message) {
        //检测队列的ID合法性
        if (queueId == null || queueId.isEmpty()) return ResultUtils.error(ErrorCode.PARAMS_ERROR, "队列的Id不能为空!");
        //如果目标队列已经存在，那么就直接向队列中添加一个消息
        if (msgQueues.containsKey(queueId)) {
            ConcurrentLinkedQueue<String> msgQueue = msgQueues.get(queueId);
            msgQueue.offer(message);
            log.info("向" + queueId + "队列中添加:" + message);
            return ResultUtils.success("添加 '" + message + "' 信息到 '" + queueId + "' 队列成功");
        }
        //如果目标队列不存在，那么就新建队列
        else {
            ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
            messages.offer(message);
            msgQueues.put(queueId, messages);
            log.info("新建" + queueId + "并添加:" + message);
            return ResultUtils.success("添加 '" + message + "' 信息到 '" + queueId + "' 队列成功");
            //return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR,"目标队列不存在");
        }
    }

    /**
     * 从某个特定的消息队列中发送信息
     *
     * @param queueId 队列的名称
     * @return 返回响应
     */
    public static BaseResponse<String> getMsgFromMQ(String queueId) {
        if (!msgQueues.containsKey(queueId)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "队列不存在");
        } else {
            ConcurrentLinkedQueue<String> msg = msgQueues.get(queueId);
            if (msg.isEmpty()) {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "队列无元素");
            } else {
                return ResultUtils.success(msg.poll());
            }
        }
    }

    /**
     * 向某个Topic中发送事件
     *
     * @param topic   topic的名称
     * @param message 消息内容
     * @return 返回响应
     */
    public static BaseResponse<String> appendToTopic(String topic, String message) {
        //检测topic的合法性
        if (topic == null || topic.isEmpty()) return ResultUtils.error(ErrorCode.PARAMS_ERROR, "topic不能为空!");
        //如果主题已经存在，那么就直接向主题中添加一个消息
        if (topics.containsKey(topic)) {
            ConcurrentLinkedQueue<String> msgList = topics.get(topic);
            msgList.offer(message);
            log.info("向" + topic + "主题中添加" + message);
        }
        //如果目标主题不存在，那么就新建一个主题
        else {
            ConcurrentLinkedQueue<String> msgList = new ConcurrentLinkedQueue<>();
            msgList.offer(message);
            topics.put(topic, msgList);
            topicUser.put(topic, new Vector<>());
            log.info("新建" + topic + "主题并添加" + message);
        }
        //这个主题需要发给所有的订阅的用户
        if (toSendUser.containsKey(topic)) {
            ConcurrentHashMap<String, Vector<String>> userAndMsg = toSendUser.get(topic);
            Iterator<Map.Entry<String, Vector<String>>> iterator = userAndMsg.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Vector<String>> temp = iterator.next();
                String key = temp.getKey();
                Vector<String> value = temp.getValue();
                value.add(message);
                log.info("向用户:" + key + "的待发送队列里面添加" + message);
            }
        } else {
            Vector<String> users = topicUser.get(topic);
            for (String user : users) {
                ConcurrentHashMap<String, Vector<String>> userAndMsg = new ConcurrentHashMap<>();
                Vector<String> msgs = new Vector<>();
                msgs.add(message);
                userAndMsg.put(user, msgs);
                toSendUser.put(topic, userAndMsg);
                log.info("向用户:" + user + "的待发送队列里面添加" + message);
            }

        }
        return ResultUtils.success("添加 '" + message + "' 信息到 '" + topic + "' 主题成功");
    }

    /**
     * 向topic注册，并开始获取消息
     *
     * @param consumerId 注册者的Id
     * @param topicId    注册的topic的id
     * @return 返回响应
     */
    public static BaseResponse<String> getMsgFromTopic(String consumerId, String topicId) {
        //如果注册的topic不存在
        if (!topics.containsKey(topicId)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "此主题不存在");
        }
        //如果是第一次注册，那么需要添加注册信息，然后把topic中所有的信息都返回给它
        Vector<String> users = topicUser.get(topicId);
        if (!users.contains(consumerId)) {
            ConcurrentHashMap<String, Vector<String>> userAndMsg = toSendUser.get(topicId);
            if (userAndMsg != null) {
                userAndMsg.put(consumerId, new Vector<>());
            }
            users.add(consumerId);
            topicUser.replace(topicId, users);
            //所有的都要发送回去
            ConcurrentLinkedQueue<String> msg = topics.get(topicId);
            Iterator<String> iterator = msg.iterator();
            StringBuilder data = new StringBuilder();
            while (iterator.hasNext()) {
                data.append(iterator.next()).append(";");
            }
            log.info("用户:" + consumerId + "第一次到来，发送topic里面所有msg:" + data);
            return ResultUtils.success(data.toString());
        }
        //如果不是第一次注册，那么只把没有发送过的信息发送回去
        ConcurrentHashMap<String, Vector<String>> toSendMsg = toSendUser.get(topicId);
        if (toSendMsg != null && !toSendMsg.isEmpty()) {
            Vector<String> msgs = toSendMsg.get(consumerId);
            if (msgs != null && !msgs.isEmpty()) {
                Iterator<String> iterator = msgs.iterator();
                StringBuilder data = new StringBuilder();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    data.append(next).append(";");
                    iterator.remove();
                    log.info("移除用户:" + consumerId + "待发送事件:" + next);
                }
                if (!data.toString().equals("")) {
                    log.info("向用户:" + consumerId + "发送:" + data);
                    return ResultUtils.success(data.toString());
                }
            }
        }
        return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "主题中事件为空");
    }

    /**
     * 向dispatcher中发布一条消息
     *
     * @param message 消息内容
     * @return 返回消息
     */
    public static BaseResponse<String> addMsg(String moduleId, String message) {
        if (msg.containsKey(moduleId)) {
            Vector<String> msgs = msg.get(moduleId);
            msgs.add(message);
        } else {
            Vector<String> temp = new Vector<>();
            temp.add(message);
            msg.put(moduleId, temp);
        }
        return ResultUtils.success("添加 '" + message + "事件成功");
    }

    /**
     * 添加模块
     *
     * @param moduleId
     * @return
     */
    public static BaseResponse<String> addModule(String moduleId) {

        //如果模块已经注册了，检查是否有没发送的消息
        if (!msg.containsKey(moduleId)) {
            msg.put(moduleId, new Vector<>());
            return ResultUtils.success("");
        } else {
            Vector<String> msgs = msg.get(moduleId);
            StringBuilder data = new StringBuilder();
            if (msgs != null && !msgs.isEmpty()) {
                Iterator<String> iterator = msgs.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    data.append(next).append(";");
                }
            }
            return ResultUtils.success(data.toString());
        }

    }
}
