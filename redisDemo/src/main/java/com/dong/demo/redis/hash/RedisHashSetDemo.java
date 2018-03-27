package com.dong.demo.redis.hash;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dong.demo.redis.cluster.RedisClusterFactory;

import redis.clients.jedis.JedisCluster;

/**
 * 1个key的value可以存储多个key-value结构的数据，类似于java里的map，主要用来存储对象数据
 * 常用命令：hset, hget , hgetall，hmset等
 * 应用场景：存储用户信息
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisHashSetDemo
{

    public static void main(String[] args) throws IOException
    {

        /**
         * 这里使用redis集群方式
         * 如不适用集群则使用注释的代码
         */
        // try (Jedis j = new Jedis("localhost", 10001))
        JedisCluster j = RedisClusterFactory.getJedisCluster();
        try
        {
            // 普通方式塞入
            j.hset("1", "name", "程咬金");
            j.hset("1", "age", "21");
            j.hset("1", "sex", "男");
            String name = j.hget("1", "name");
            System.out.println(name);

            // map方式塞入
            Map<String, String> map = new HashMap<>();
            map.put("name", "吕布");
            map.put("age", "22");
            map.put("sex", "男");
            j.hmset("2", map);

            // hgetall
            Map<String, String> data1 = j.hgetAll("1");
            System.out.println(data1);
            // mget
            List<String> list = j.hmget("2", "age", "sex", "name");
            System.out.println(list);
        }
        finally
        {

        }

    }
}
