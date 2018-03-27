package com.dong.test.zookeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.I0Itec.zkclient.ZkClient;

public class MainDemo
{

    private static CountDownLatch latch = new CountDownLatch(100);

    public static void main(String[] args)
    {

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
            threadPool.submit(new MainDemo().new DistributeRunnable());
        }
        threadPool.shutdown();
    }

    class DistributeRunnable implements Runnable
    {

        @Override
        public void run()
        {
            DistributedLock lockMutex = new SimpleDistributedLockMutex(new ZkClient("localhost:2181"), "/lock");
            try
            {
                System.out.println(Thread.currentThread().getName() + "等待锁");
                lockMutex.acquire();
                System.out.println(Thread.currentThread().getName() + "获取到锁");
                lockMutex.release();
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                latch.countDown();
            }
        }

    }

}
