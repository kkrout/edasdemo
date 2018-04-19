package com.dong.demo.rocket;

import java.util.UUID;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;

/**
 * 消息生产者
 * 用于发送消息
 * 
 * @author zhdong
 * @version 1.0, 2018年3月27日
 * @since JDK1.8
 */
public class RocketMQProducer
{

    private DefaultMQProducer sender;

    protected String nameServer;

    protected String groupName;

    protected String topics;

    /**
     * 初始化
     * 
     * @author zhdong
     */
    public void init()
    {
        // 组名
        sender = new DefaultMQProducer(groupName);
        // 服务器名称
        sender.setNamesrvAddr(nameServer);
        sender.setInstanceName(UUID.randomUUID().toString());
        sender.setVipChannelEnabled(false);
        try
        {
            sender.start();
        }
        catch (MQClientException e)
        {
            e.printStackTrace();
        }
    }

    public RocketMQProducer(String nameServer, String groupName, String topics)
    {
        this.nameServer = nameServer;
        this.groupName = groupName;
        this.topics = topics;
    }

    /**
     * 发送消息
     * 
     * @author zhdong
     * @param message
     */
    public void send(Message message)
    {
        // 设置队列集合
        message.setTopic(topics);
        // 设置队列标签tags，sz深圳
        message.setTags("sz");

        try
        {
            SendResult result = sender.send(message);
            SendStatus status = result.getSendStatus();
            System.out.println("messageId=" + result.getMsgId() + ", status=" + status);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}