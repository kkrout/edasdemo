package com.dong.demo.test.rocket;

import com.alibaba.rocketmq.common.message.Message;
import com.dong.demo.rocket.RocketMQProducer;

public class RocketMQProducerTest
{
    public static void main(String[] args)
    {
        String mqNameServer = "39.105.15.80:9876";
        // 定义topic
        String mqTopics = "MQ-MSG-TOPICS-TEST";

        // 定义组
        String mqGroupName = "CONSUMER-MQ-GROUP";

        // 初始化消息链接
        RocketMQProducer mqProducer = new RocketMQProducer(mqNameServer, mqGroupName, mqTopics);
        mqProducer.init();

        // 发送消息
        for (int i = 0; i < 5; i++)
        {

            Message message = new Message();
            message.setBody(("I send message to RocketMQ " + i).getBytes());
            mqProducer.send(message);
        }
    }
}
