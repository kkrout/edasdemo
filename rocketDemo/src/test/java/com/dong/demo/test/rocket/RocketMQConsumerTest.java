package com.dong.demo.test.rocket;

import com.dong.demo.rocket.RocketMQConsumer;
import com.dong.demo.rocket.RocketMQListener;

public class RocketMQConsumerTest
{

    public static void main(String[] args)
    {
        consumer1();
        // consumer2();
        // consumer3();
    }

    public static void consumer1()
    {
        String mqNameServer = "39.105.15.80:9876";
        // 定义topic
        String mqTopics = "MQ-MSG-TOPICS-TEST";

        // 定义组
        String mqGroupName = "CONSUMER-MQ-GROUP";
        // 监听客户端接收的消息
        RocketMQListener mqListener = new RocketMQListener("consumer1");
        // 消息客户端
        RocketMQConsumer mqConsumer = new RocketMQConsumer(mqListener, mqNameServer, mqGroupName, mqTopics);
        // 启动客户端监听消息
        mqConsumer.init();
    }

    public static void consumer2()
    {
        String mqNameServer = "39.105.15.80:9876";
        // 定义topic
        String mqTopics = "MQ-MSG-TOPICS-TEST";

        // 定义组
        String mqGroupName = "CONSUMER-MQ-GROUP";
        // 监听客户端接收的消息
        RocketMQListener mqListener = new RocketMQListener("consumer2");
        // 消息客户端
        RocketMQConsumer mqConsumer = new RocketMQConsumer(mqListener, mqNameServer, mqGroupName, mqTopics);
        // 启动客户端监听消息
        mqConsumer.init();
    }

    public static void consumer3()
    {
        String mqNameServer = "39.105.15.80:9876";
        // 定义topic
        String mqTopics = "MQ-MSG-TOPICS-TEST";

        // 定义组
        String mqGroupName = "CONSUMER-MQ-GROUP";
        // 监听客户端接收的消息
        RocketMQListener mqListener = new RocketMQListener("consumer3");
        // 消息客户端
        RocketMQConsumer mqConsumer = new RocketMQConsumer(mqListener, mqNameServer, mqGroupName, mqTopics);
        // 启动客户端监听消息
        mqConsumer.init();
    }

}
