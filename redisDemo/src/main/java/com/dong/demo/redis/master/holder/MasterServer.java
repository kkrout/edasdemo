package com.dong.demo.redis.master.holder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class MasterServer implements Runnable
{
    // 服务列表
    private static String mastList = "3333;4444;5555;6666";//

    private static final Map<String, Set<String>> conCurMap = new ConcurrentHashMap<>();

    private int port;

    private List<String> listeners = new Vector<>();

    public MasterServer(int port)
    {
        this.port = port;
        String[] list = mastList.split(";");
        // 建立各服务之间的连接
        for (int i = 0; i < list.length; i++)
        {
            int sport = Integer.parseInt(list[i]);
            new Thread(new MasterListener(port, sport, listeners)).start();
        }

        // 监听投票结果
        new Thread()
        {

            @Override
            public void run()
            {
                while (true)
                {
                    Set<String> set = conCurMap.get(port + "");
                    if (set == null)
                    {
                        continue;
                    }
                    // 票数大于一半，自认为是master
                    if (set.size() > 2)
                    {
                        System.out.println("别怀疑，我就是master");
                        break;
                    }
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @Override
    public void run()
    {
        ServerSocket ss;
        try
        {
            ss = new ServerSocket(port);
            while (true)
            {
                Socket accept = ss.accept();
                // 读取投票信息
                new Reader(accept, port);
                // 响应客户端
                new Writer(accept, port);
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    class Reader extends Thread
    {

        private int port;
        private Socket accept;

        public Reader(Socket accept, int port)
        {
            this.accept = accept;
            this.port = port;
            this.start();
        }

        /**
         * [简要描述]:<br/>
         * [详细描述]:<br/>
         * 
         * @author zhdong
         * @exception
         *                @see
         *                java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            // 投票者
            String from = null;
            Set<String> st = null;
            try
            {
                while (true)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                    String msg = null;
                    // 读取投票信息
                    while ((msg = br.readLine()) != null)
                    {
                        if (msg.startsWith("TP:"))
                        {
                            String[] tpinfo = msg.split(":");
                            from = tpinfo[1];
                            String to = tpinfo[2];
                            if (to.equals(port + ""))
                            {
                                synchronized (conCurMap)
                                {
                                    st = conCurMap.get(to);
                                    if (st == null)
                                    {
                                        st = new HashSet<>();
                                        conCurMap.put(to, st);
                                    }
                                    st.add(from);
                                }
                                System.out.println("投票结果：" + st);
                            }
                        }
                    }
                }
            }
            catch (IOException e)
            {
                if (st != null && from != null)
                {
                    System.out.println("支持者倒下了。。。");
                    st.remove(from);
                }
            }
        }
    }

    class Writer extends Thread
    {

        private int port;
        private Socket accept;

        public Writer(Socket accept, int port)
        {
            this.accept = accept;
            this.port = port;
            this.start();
        }

        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    PrintWriter pw = new PrintWriter(accept.getOutputStream());
                    pw.println(port + "");
                    pw.flush();
                    Thread.sleep(200);
                }
            }
            catch (IOException | InterruptedException e)
            {
                System.out.println("支持者倒下了。。。。");
            }
        }

    }

}
