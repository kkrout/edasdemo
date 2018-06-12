package com.dong.demo.redis.master.holder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class MasterListener implements Runnable
{
    private int sport;

    private int myport;

    private Socket s;

    private List<String> listeners;

    public MasterListener(int myport, int sport, List<String> listeners)
    {
        this.myport = myport;
        this.sport = sport;
        this.listeners = listeners;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                // 链接服务端，检查是否能联调
                s = new Socket("localhost", sport);
                // 如果能连通，添加服务可用列表
                if (!listeners.contains(sport + ""))
                {
                    listeners.add(sport + "");
                }

                int serSize = 0;
                // 挂起连接
                while (true)
                {
                    InputStream in = s.getInputStream();
                    in.read();

                    int size = listeners.size();
                    if (size > 0 && serSize != size)
                    {
                        String[] array = listeners.toArray(new String[] {});
                        Arrays.sort(array);
                        System.out.println(Thread.currentThread().getName() + "投票给：" + array[0]);
                        PrintWriter pw = new PrintWriter(s.getOutputStream());
                        pw.println("TP:" + myport + ":" + array[0]);
                        pw.flush();
                        serSize = size;

                        // TODO 服务发生变化，需要从新汇总投票
                    }

                }
            }
            catch (IOException e)
            {
                // System.out.println(sport + "，连接断开。。。");
                // 服务端口，移除列表
                if (listeners.contains(sport + ""))
                {
                    System.out.println("服务端口，移除服务：" + sport);
                    listeners.remove(sport + "");
                }
            }
            // System.out.println(sport + "连接断开，尝试重试连接，沉睡线程.....");
            // 尝试重试连接，沉睡线程
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
