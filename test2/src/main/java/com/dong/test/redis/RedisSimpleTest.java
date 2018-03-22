package com.dong.test.redis;

import redis.clients.jedis.Jedis;

public class RedisSimpleTest
{

    public static void main(String[] args)
    {
        for (int i = 0; i < 20; i++)
        {
            System.out.println("启动任务:" + i);
            new Thread(new RedisRunable(i + "")).start();
        }
    }

}

class RedisRunable implements Runnable
{
    private String v;

    public RedisRunable(String v)
    {
        this.v = v;
    }

    @Override
    public void run()
    {
        Jedis j = new Jedis("localhost", 10001);
        String s = j.get("hello");
        System.out.println(s);
        j.set("hello", v);
        j.close();
    }

}
