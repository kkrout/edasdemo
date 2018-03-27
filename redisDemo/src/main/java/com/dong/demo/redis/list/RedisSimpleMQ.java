package com.dong.demo.redis.list;

import java.io.IOException;

import com.dong.demo.redis.cluster.RedisClusterFactory;

import redis.clients.jedis.JedisCluster;

/**
 * 描述:列表类型，可以从先进后出，后进先出，使用其特性可以实现队列和栈。
 * 常用命令：lpush，rpush，lpop，rpop，lrange等（l就是left，r就是right，push是插入，pop是获取）
 * 应用场景：消息队列
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisSimpleMQ
{

    public static void main(String[] args)
    {
        RedisSimpleMQ MQ = new RedisSimpleMQ();
        Producer producer = MQ.new Producer("test");
        Consumer consumer1 = MQ.new Consumer("test");
        Consumer consumer2 = MQ.new Consumer("test");
        Consumer consumer3 = MQ.new Consumer("test");
        producer.start();
        consumer1.start();
        consumer2.start();
        consumer3.start();

    }

    /**
     * 消息生产者
     *
     * @author zhdong
     * @version 1.0, 2018年3月26日
     * @since JDK1.8
     */
    class Producer extends Thread
    {
        private String topic;

        public Producer(String topic)
        {
            /**
             * redis集群的key采用的是keySlot算法，通过次算法将key分布到不同的slot上去了，
             * 如果要将list的一个value移动另外一个list中去，两个list的key不在同一个slot下，
             * 就会报错，解决办法就是让key都能分布到同一个slot，通过把key放到{}之间可以使key分布到同一各slot中。
             */
            this.topic = "{" + topic + "}";
        }

        public void sendMessage() throws IOException
        {
            /**
             * 这里使用redis集群方式
             * 如不适用集群则使用注释的代码
             */
            // try (Jedis j = new Jedis("localhost", 10001))
            try (JedisCluster j = RedisClusterFactory.getJedisCluster())
            {
                int count = 0;
                // 1w个为什么
                while (true && count < 10)
                {
                    j.lpush(topic, count++ + "");
                }
            }
        }

        @Override
        public void run()
        {
            try
            {
                sendMessage();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 消息消费者
     *
     * @author zhdong
     * @version 1.0, 2018年3月26日
     * @since JDK1.8
     */
    class Consumer extends Thread
    {
        private String topic;

        public Consumer(String topic)
        {
            this.topic = "{" + topic + "}";
        }

        @Override
        public void run()
        {
            /**
             * 这里使用redis集群方式
             * 如不适用集群则使用注释的代码
             */
            // try (Jedis j = new Jedis("localhost", 10001))
            try (JedisCluster j = RedisClusterFactory.getJedisCluster())
            {
                int TIME_OUT = 3;
                int count = 0;
                while (true)
                {

                    // 最开始先保存队列
                    String curQuenue = j.brpoplpush(topic, topic + "_BAK", TIME_OUT);
                    if (curQuenue == null)
                        break;
                    count++;

                    System.out.println(Thread.currentThread().getName() + "抢到消息为：" + curQuenue);

                    // 出现异常
                    if (count % 3 == 0)
                    {
                        System.out.println(curQuenue + ":出现异常");
                        continue;
                    }

                    String bakQuenue = j.rpop(topic + "_BAK");
                    System.out.println(bakQuenue + "备份队列已清除");

                }
            }
            // catch (InterruptedException e)
            // {
            // e.printStackTrace();
            // }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
