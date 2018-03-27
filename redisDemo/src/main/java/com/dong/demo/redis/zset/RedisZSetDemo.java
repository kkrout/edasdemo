package com.dong.demo.redis.zset;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * 描述：相对于set，zset增加了有序的特性，另外zset还可以设置优先级，优先级高的将排序靠前
 * 常用命令：zadd，zrange，zrem，zcard等
 * 应用场景：排名
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisZSetDemo
{
    public static void main(String[] args)
    {
        try (Jedis j = new Jedis("localhost", 10001))
        {

            // 初始化
            j.del("myZset");

            for (int i = 0; i < 100; i++)
            {
                double score = (int) (Math.random() * 1000);
                j.zadd("myZset", score, String.valueOf(i));
            }

            // 前10名
            System.out.println("后10名如下：");
            Set<String> zrange = j.zrange("myZset", 0, 10);
            for (Iterator<String> iterator = zrange.iterator(); iterator.hasNext();)
            {
                String mem = iterator.next();
                double zrem = j.zscore("myZset", mem);
                System.out.println("\tID:" + mem + "--成绩:" + zrem);
            }

            // 前10名,zrevrange降序获取
            System.out.println("前10名如下：");
            Set<String> zrevrange = j.zrevrange("myZset", 0, 10);
            for (Iterator<String> iterator = zrevrange.iterator(); iterator.hasNext();)
            {
                String mem = iterator.next();
                double zrem = j.zscore("myZset", mem);
                System.out.println("\tID:" + mem + "--成绩:" + zrem);
            }

        }
    }
}
