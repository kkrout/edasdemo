package com.dong.demo.redis.set;

import java.io.IOException;
import java.util.Set;

import com.dong.demo.redis.cluster.RedisClusterFactory;

import redis.clients.jedis.JedisCluster;

/**
 * 无序集合且不重复，利用这一特性可以存储做数据去重处理
 * 常用命令：sadd,spop,smembers,sunion
 * 应用场景：各种集合操作，如：去重，交集，并集，差集等
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisSetDemo
{

    public static void main(String[] args) throws IOException
    {
        /**
         * 这里使用redis集群方式
         * 如不适用集群则使用注释的代码
         */
        // try (Jedis j = new Jedis("localhost", 10001))
        try (JedisCluster j = RedisClusterFactory.getJedisCluster())
        {

            // 初始化
            j.del("myset");
            j.del("set001");
            j.del("set002");

            for (int i = 0; i < 100; i++)
            {
                int v = (int) (Math.random() * 10);
                j.sadd("myset", String.valueOf(v));
            }
            // 默认去除重复数据
            System.out.println(j.smembers("myset"));

            for (int i = 0; i < 5; i++)
            {
                int v = (int) (Math.random() * 10);
                j.sadd("set001", String.valueOf(v));
            }

            for (int i = 0; i < 5; i++)
            {
                int v = (int) (Math.random() * 10);
                j.sadd("set002", String.valueOf(v));
            }

            System.out.println("set001:" + j.smembers("set001"));
            System.out.println("set002:" + j.smembers("set002"));

            // 找出set001与set002的交集
            Set<String> sinter = j.sinter("set001", "set002");
            System.out.println("set001与set002的交集:" + sinter);

            // 找出set001不存在set002的差集
            Set<String> sdiff = j.sdiff("set001", "set002");
            System.out.println("set001与set002的差集:" + sdiff);

            // 找出set001和set002的并集
            Set<String> sunion = j.sunion("set001", "set002");
            System.out.println("set001与set002的并集:" + sunion);

        }
    }

}
