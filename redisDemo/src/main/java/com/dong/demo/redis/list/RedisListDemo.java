package com.dong.demo.redis.list;

import java.io.IOException;
import java.util.List;

import com.dong.demo.redis.cluster.RedisClusterFactory;

import redis.clients.jedis.JedisCluster;

/**
 * 描述:列表类型，可以从先进后出，后进先出，使用其特性可以实现队列和栈。
 * 常用命令：lpush，rpush，lpop，rpop，lrange等（l就是left，r就是right，push是插入，pop是获取）
 * 应用场景：简单用法
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisListDemo
{

    public static void main(String[] args) throws IOException
    {
        /*
         * 利用lpush + rpop，进行先进先出，模拟队列
         * 1,2,3,4... > ---list[...4,3,2,1]--- > 1,2,3,4...
         */
        /**
         * 这里使用redis集群方式
         * 如不适用集群则使用注释的代码
         */
        // try (Jedis j = new Jedis("localhost", 10001))
        try (JedisCluster j = RedisClusterFactory.getJedisCluster())
        {
            // 塞入队列
            for (int i = 0; i < 20; i++)
            {
                j.lpush("list_", String.valueOf(i));
            }
            // 获取队列
            for (int i = 0; i < 50; i++)
            {
                String d1 = j.rpop("list_");
                System.out.println("从右边拿过来塞到左边：" + d1);
                // 阻塞的拿，当无消息时，进入阻塞状态
                List<String> brpop = j.brpop(1000, "list_");
                System.out.println("一次只拿一个:" + brpop);

                String s = j.rpoplpush("list_", "list_bak");
                System.out.println("从一个列表拿到另外一个列表:" + s);
            }

        }

    }
}
