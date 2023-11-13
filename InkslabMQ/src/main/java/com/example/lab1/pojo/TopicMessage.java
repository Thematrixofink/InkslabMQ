package com.example.lab1.pojo;

import lombok.Data;

@Data
public class TopicMessage {
    private String message;
    //存活时间，单位为毫秒
    private Long survivalTime;
}
