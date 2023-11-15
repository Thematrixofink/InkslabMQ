package com.example.lab1.middleware;

import com.example.lab1.common.BaseResponse;
import com.example.lab1.common.ErrorCode;
import com.example.lab1.common.ResultUtils;
import com.example.lab1.pojo.SubList;


import com.example.lab1.pojo.TopicMessage;
import lombok.Synchronized;
import lombok.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//提供相应的中间件方法
@Service
@Slf4j
public class MiddleWare {
    //消息，key为module的id，Value为需要发送给当前module的消息
    private static final ConcurrentHashMap<String, Vector<String>> msg = new ConcurrentHashMap<>();
    //消息队列  Key为队列的ID，Value相应的队列
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> msgQueues = new ConcurrentHashMap<>();
    //Topic key为topic的名称，value为消息
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<TopicMessage>> topics = new ConcurrentHashMap<>();
    //保存topic中哪个消息没有被收到，Key为topic，Value为Map<订阅者Id,需要发送给订阅者的消息>
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Vector<String>>> toSendUser = new ConcurrentHashMap<>();
    //保存订阅响应主题的用户,Key为topic， Value为订阅此主题的用户
    private static final ConcurrentHashMap<String, Vector<String>> topicUser = new ConcurrentHashMap<>();

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
     * @param surTime 消息的存活时间
     * @return 返回响应
     */
    public static BaseResponse<String> appendToTopic(String topic, String message, Long surTime) {
        //检测topic的合法性
        if (topic == null || topic.isEmpty()) return ResultUtils.error(ErrorCode.PARAMS_ERROR, "topic不能为空!");
        //如果主题已经存在，那么就直接向主题中添加一个消息
        if (!topics.containsKey(topic)) {
            ConcurrentLinkedQueue<TopicMessage> msgList = new ConcurrentLinkedQueue<>();
            topics.put(topic, msgList);
            topicUser.put(topic, new Vector<>());
        }
        ConcurrentLinkedQueue<TopicMessage> msgList = topics.get(topic);
        TopicMessage temp = new TopicMessage();
        temp.setMessage(message);
        long l = System.currentTimeMillis() + surTime;
        temp.setSurvivalTime(l);
        msgList.offer(temp);
        log.info("向" + topic + "主题中添加:" + temp.getMessage() + ",其存活时间为:" + temp.getSurvivalTime() + "ms");
        //这个主题需要发给所有的订阅的用户
        if (toSendUser.containsKey(topic)) {
            ConcurrentHashMap<String, Vector<String>> userAndMsg = toSendUser.get(topic);
            Iterator<Map.Entry<String, Vector<String>>> iterator = userAndMsg.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Vector<String>> temp2 = iterator.next();
                String key = temp2.getKey();
                Vector<String> value = temp2.getValue();
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
            ConcurrentLinkedQueue<TopicMessage> msg = topics.get(topicId);
            Iterator<TopicMessage> iterator = msg.iterator();
            StringBuffer data = new StringBuffer();
            while (iterator.hasNext()) {
                TopicMessage next = iterator.next();
                Long survivalTime = next.getSurvivalTime();
                if(survivalTime.compareTo(System.currentTimeMillis()) >= 0) {
                    data.append(next.getMessage()).append(";");
                }
                else{
                    iterator.remove();
                }
            }
            log.info("用户:" + consumerId + "第一次到来，发送topic里面所有msg:" + data);
            return ResultUtils.success(data.toString());
        }
        //如果不是第一次注册，那么只把没有发送过的信息发送回去
        ConcurrentHashMap<String, Vector<String>> toSendMsg = toSendUser.get(topicId);
        if (toSendMsg != null && !toSendMsg.isEmpty()) {
            Vector<String> msgs = toSendMsg.get(consumerId);
            if (msgs != null && !msgs.isEmpty()) {
                synchronized (msgs) {
                    Iterator<String> iterator = msgs.iterator();
                    StringBuffer data = new StringBuffer();
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
        }
        return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "主题中事件为空");
    }

    /**
     * 向dispatcher中发布一条消息
     *
     * @param message 消息内容
     * @return 返回消息
     */
    public static BaseResponse<String> addMsg(String message) {
        if (msg.isEmpty()) return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "无注册模块，消息不会被接受");
        Iterator<Map.Entry<String, Vector<String>>> iterator = msg.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Vector<String>> temp = iterator.next();
            String key = temp.getKey();
            Vector<String> value = temp.getValue();
            value.add(message);
            log.info("向用户:" + key + "的待发送队列里面添加" + message);
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
            return ResultUtils.success("模块注册成功!");
        } else {
            Vector<String> msgs = msg.get(moduleId);
            StringBuffer data = new StringBuffer();
            if (msgs != null && !msgs.isEmpty()) {
                Iterator<String> iterator = msgs.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    data.append(next).append(";");
                    iterator.remove();
                }
            }
            if (data.length() > 0) {
                return ResultUtils.success(data.toString());
            } else {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未获取到信息");
            }
        }

    }
}
