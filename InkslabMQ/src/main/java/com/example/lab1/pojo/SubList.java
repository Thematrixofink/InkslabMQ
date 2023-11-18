package com.example.lab1.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubList {
    public Set<String> subscribeTopicSet;//表示订阅主题（指定消息应该发送到哪些订阅者，一个订阅者可选择一个或者多个订阅主题）
    public Set<String> subscribePubSet;//表示订阅发布者（消息的发送方，只需要将消息发送到对应主题上即可）
}
