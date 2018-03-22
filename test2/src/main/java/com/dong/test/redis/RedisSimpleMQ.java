package com.dong.test.redis;

import redis.clients.jedis.Jedis;

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

    class Producer extends Thread
    {
        private String topic;

        public Producer(String topic)
        {
            this.topic = topic;
        }

        public void sendMessage()
        {
            try (Jedis j = new Jedis("localhost", 10001))
            {
                int count = 0;
                // 1w个为什么
                while (true && count < 10000)
                {
                    j.lpush(topic, count++ + "");
                    // Thread.sleep(200);
                }
            }
            // catch (InterruptedException e)
            // {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        }

        @Override
        public void run()
        {
            sendMessage();
        }

    }

    class Consumer extends Thread
    {
        private String topic;

        public Consumer(String topic)
        {
            this.topic = topic;
        }

        @Override
        public void run()
        {
            try (Jedis j = new Jedis("localhost", 10001))
            {
                long timeout = 0;
                long start = 0;
                long TIME_OUT = 3000;
                while (true && timeout < TIME_OUT)
                {

                    String v = j.rpop(topic);

                    // 无消息时，进入等待，设置等待超时时间
                    if (v == null || "".equals(v))
                    {
                        if (start == 0)
                            start = System.currentTimeMillis();
                        timeout = System.currentTimeMillis() - start;
                    }
                    else
                    {
                        // 获得消息重置等待时间
                        start = 0;
                        timeout = 0;
                        System.out.println(Thread.currentThread().getName() + "抢到消息为：" + v);
                    }

                    // Thread.sleep(10);
                }

            }
            // catch (InterruptedException e)
            // {
            // e.printStackTrace();
            // }
        }

    }

}
