package com.dong.demo.redis.string;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dong.demo.redis.cluster.RedisClusterFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * string是Redis最基本的数据类型，key就是string类型，最长能存512M，value没有限制，以实际内存为主
 * 常用命令：get，set，incr(自动增长，只能是整性数据，否则会报错)，decr(反增长，与incr相反)，mget等
 * 应用场景：数据缓存
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisStringDemo
{

    // 不存在才插入
    private static final String SET_IF_NOT_EXIST = "NX";
    // 过期时间设置
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private Jedis jedis;

    private Jedis getRedisConnect()
    {
        if (jedis == null)
        {
            jedis = new Jedis("localhost", 10001);
        }
        else if (!jedis.isConnected())
        {
            jedis.connect();
        }
        return jedis;
    }

    private void closeRedisConnect()
    {
        if (jedis != null && !jedis.isConnected())
            jedis.close();
    }

    /**
     * 模拟从数据库读取用户id
     * 
     * @author zhdong
     * @param uid
     * @return
     */
    public String getUserName(String uid)
    {
        // Jedis jedis2 = getRedisConnect();
        JedisCluster jedis2 = RedisClusterFactory.getJedisCluster();
        final String KEY_PREFIX = "USER_NAME_";
        String key = KEY_PREFIX + uid;
        try
        {
            // 优先从缓存中获取
            String name = jedis2.get(key);
            // 如果缓存没有，从数据库获取并且塞入缓存
            if (name == null)
            {
                String databaseUserName = getDatabaseUserName(uid);
                // 如果数据库不存在数据，则不设置缓存
                if (databaseUserName != null)
                {
                    // key，value，插入方式:不存在插入，过期时间，时间数
                    jedis2.set(key, databaseUserName, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 30000);
                }
                return databaseUserName;
            }
            System.out.println("从缓存中获取到数据..");
            return name;
        }
        finally
        {
            // closeRedisConnect();
            try
            {
                jedis2.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private String getDatabaseUserName(String uid)
    {
        Map<String, String> map = new HashMap<>();
        map.put("1", "亚瑟");
        map.put("2", "程咬金");
        map.put("3", "吕布");
        map.put("4", "钟馗");

        String v = map.get(uid);
        if (v == null)
        {
            return null;
        }
        System.out.println("从数据库获取数据");

        return v;
    }

    public static void main(String[] args)
    {
        RedisStringDemo demo = new RedisStringDemo();
        String name = demo.getUserName("2");
        System.out.println(name);
    }
}
