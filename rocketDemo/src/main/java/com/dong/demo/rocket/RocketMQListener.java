package com.dong.demo.rocket;

import java.util.List;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * 消息监听者
 * 用于客户端接收消息后，触发监听进行消息处理
 * 
 * @author zhdong
 * @version 1.0, 2018年3月27日
 * @since JDK1.8
 */
public class RocketMQListener implements MessageListenerConcurrently
{
    private String name;

    public RocketMQListener(String name)
    {
        this.name = name;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
    {
        for (MessageExt message : msgs)
        {

            String msg = new String(message.getBody());
            System.out.println("[" + name + "]recived:msg data from rocketMQ:" + msg);
        }
        // 返回成功表示通知队列移除消息
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
