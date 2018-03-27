package com.dong.demo.rocket;

import java.util.UUID;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

/**
 * 消息消费者
 * 用于接收消息队列的消息，收到消息后触发监听事件
 * 
 * @author zhdong
 * @version 1.0, 2018年3月27日
 * @since JDK1.8
 */
public class RocketMQConsumer
{

    private DefaultMQPushConsumer consumer;

    private MessageListener listener;

    protected String nameServer;

    protected String groupName;

    protected String topics;

    public RocketMQConsumer(MessageListener listener, String nameServer, String groupName, String topics)
    {
        this.listener = listener;
        this.nameServer = nameServer;
        this.groupName = groupName;
        this.topics = topics;
    }

    /**
     * 初始化客户端
     * 
     * @author zhdong
     */
    public void init()
    {
        consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(nameServer);
        try
        {
            // 订阅消息队列集合，接收队列集合中的所有tags
            consumer.subscribe(topics, "*");
            // 可以指定tags
            // consumer.subscribe(topics, "sz");
        }
        catch (MQClientException e)
        {
            e.printStackTrace();
        }
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 这里需要注册listener，单独写个listener类进行事件回调
        consumer.registerMessageListener((MessageListenerConcurrently) this.listener);

        try
        {
            consumer.start();
        }
        catch (MQClientException e)
        {
            e.printStackTrace();
        }
        System.out.println("RocketMQConsumer Started! group=" + consumer.getConsumerGroup() + " instance="
                + consumer.getInstanceName());
    }

}
