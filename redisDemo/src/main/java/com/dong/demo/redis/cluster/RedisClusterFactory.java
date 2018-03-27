package com.dong.demo.redis.cluster;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * redis集群链接工厂
 *
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisClusterFactory
{

    public static JedisCluster getJedisCluster()
    {
        Set<HostAndPort> nodes = new HashSet<>();
        String[] serverArray = "127.0.0.1:6001 127.0.0.1:6002 127.0.0.1:6003 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003"
                .split(" ");
        for (String ipPort : serverArray)
        {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        // 注意：这里超时时间不要太短，他会有超时重试机制。而且其他像httpclient、dubbo等RPC框架也要注意这点
        return new JedisCluster(nodes, 1000, 1000, 1, null, new GenericObjectPoolConfig());
    }

}
