package com.dong.demo.redis.string;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dong.demo.redis.cluster.RedisClusterFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * string是Redis最基本的数据类型，key就是string类型，最长能存512M，value没有限制，以实际内存为主
 * 常用命令：get，set，incr(自动增长，只能是整性数据，否则会报错)，decr(反增长，与incr相反)，mget等
 * 应用场景：分布式锁
 * 
 * @author zhdong
 * @version 1.0, 2018年3月26日
 * @since JDK1.8
 */
public class RedisDistributLockDemo
{

    // 命令执行结果
    private static final String RELEASE_SUCCESS = "OK";
    // 不存在才插入
    private static final String SET_IF_NOT_EXIST = "NX";
    // 过期时间设置
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    // 多线程测试标记
    private static CountDownLatch latch = new CountDownLatch(100);

    private static int i = 0;

    /**
     * 获得锁，没有得到锁进入等待
     * 
     * @author zhdong
     * @param key
     * @param timeout
     * @return
     * @throws IOException
     */
    public static boolean getLock(String key, int timeout) throws IOException
    {
        /**
         * 这里使用redis集群方式
         * 如不适用集群则使用注释的代码
         */
        // try (Jedis j = new Jedis("localhost", 10001))
        try (JedisCluster j = RedisClusterFactory.getJedisCluster())
        {
            // 访问redis原子性
            String result = j.set(key, "1", SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeout);
            if (RELEASE_SUCCESS.equals(result))
            {
                System.out.println("获取到锁:" + Thread.currentThread().getName());
                j.setex(key, timeout, "1");
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public static boolean getLock2(Jedis j, String key, int timeout)
    {
        // 访问redis原子性
        String result = j.set(key, "1", SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, timeout);
        if (RELEASE_SUCCESS.equals(result))
        {
            System.out.println("获取到锁:" + Thread.currentThread().getName());
            j.setex(key, timeout, "1");
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void waitLock() throws IOException
    {
        boolean hasLock = false;
        String key = "LOCKER";
        try
        {
            while (true)
            {
                // 重试获取锁
                if (RedisDistributLockDemo.getLock(key, 10000))
                {
                    hasLock = true;
                    RedisDistributLockDemo.realseLock(key, 10000);
                    break;
                }
            }
        }
        finally
        {
            if (hasLock)
            {
                RedisDistributLockDemo.realseLock(key, 10000);
            }
        }
    }

    public static boolean realseLock(String key, int timeout) throws IOException
    {
        /**
         * 这里使用redis集群方式
         * 如不适用集群则使用注释的代码
         */
        // try (Jedis j = new Jedis("localhost", 10001))
        try (JedisCluster j = RedisClusterFactory.getJedisCluster())
        {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = j.eval(script, Collections.singletonList(key), Collections.singletonList("1"));
            if (RELEASE_SUCCESS.equals(result))
            {
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args)
    {
        RedisDistributLockDemo distributLock = new RedisDistributLockDemo();
        ExecutorService threadPool = Executors.newFixedThreadPool(1000);
        final long start = System.nanoTime();

        new Thread()
        {
            public void run()
            {
                try
                {
                    latch.await();
                    System.out.println("验证结果：" + i);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println("运行完成耗时：" + (System.nanoTime() - start));

            };
        }.start();

        for (int i = 0; i < 100; i++)
        {
            threadPool.submit(distributLock.new DistributeRunnable());
        }
        threadPool.shutdown();
    }

    class DistributeRunnable implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                RedisDistributLockDemo.waitLock();
                System.out.println(Thread.currentThread().getName() + "正在处理数据。。。");
                i++;
                Thread.sleep((int) (Math.random() * 5000));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            latch.countDown();
        }

    }

}
