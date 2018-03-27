package com.dong.test.redis;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;

public class DistributLock
{

    static String RELEASE_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static CountDownLatch latch = new CountDownLatch(100);

    public static boolean waitLock(String key, int timeout)
    {
        try (Jedis j = new Jedis("localhost", 10001))
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

    public static boolean realseLock(String key, int timeout)
    {
        try (Jedis j = new Jedis("localhost", 10001))
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
        DistributLock distributLock = new DistributLock();
        ExecutorService threadPool = Executors.newFixedThreadPool(1000);
        final long start = System.nanoTime();

        new Thread()
        {
            public void run()
            {
                try
                {
                    latch.await();
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
            boolean hasLock = false;
            String key = "LOCKER";
            try
            {
                while (true)
                {
                    if (DistributLock.waitLock(key, 10000))
                    {
                        hasLock = true;
                        DistributLock.realseLock(key, 10000);
                        break;
                    }
                }
            }
            finally
            {
                if (hasLock)
                {
                    DistributLock.realseLock(key, 10000);
                }
                latch.countDown();
            }
        }

    }

}
