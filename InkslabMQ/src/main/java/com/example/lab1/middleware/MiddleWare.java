package com.example.lab1.middleware;

import com.example.lab1.common.BaseResponse;

public interface MiddleWare {


        /**
         * 向某个特定的消息队列中添加信息
         *
         * @param queueId 队列的ID
         * @param message 发布的信息
         */
        BaseResponse<String> addMegToMQ(String queueId, String message);


        /**
         * 从某个特定的消息队列中发送信息
         *
         * @param queueId 队列的名称
         * @return 返回响应
         */
        BaseResponse<String> getMsgFromMQ(String queueId);

        /**
         * 向某个Topic中发送事件
         *
         * @param topic   topic的名称
         * @param message 消息内容
         * @param surTime 消息的存活时间
         * @return 返回响应
         */
        BaseResponse<String> appendToTopic(String topic, String message, Long surTime);

        /**
         * 向topic注册，并开始获取消息
         *
         * @param consumerId 注册者的Id
         * @param topicId    注册的topic的id
         * @return 返回响应
         */
        BaseResponse<String> getMsgFromTopic(String consumerId, String topicId);

        /**
         * 向dispatcher中发布一条消息
         *
         * @param message 消息内容
         * @return 返回消息
         */
        BaseResponse<String> addMsg(String message);

        /**
         * 添加模块
         *
         * @param moduleId
         * @return
         */
        BaseResponse<String> addModule(String moduleId);

}
