package com.dong.demo.redis.master.holder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MasterHolder
{

    public static void main(String[] args) throws Exception
    {
        new Thread(new MasterServer(Integer.parseInt(args[0]))).start();
        // new Thread(new MasterServer(4444)).start();
        // new Thread(new MasterServer(5555)).start();
        // new Thread(new MasterServer(6666)).start();
    }

}

class Master extends Thread
{

    private String mastList = "3333;4444;5555;6666";

    private int port;

    private List<String> listeners = new ArrayList<>();

    public Master(int port)
    {
        this.port = port;
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    ServerSocket ss = new ServerSocket(port);
                    while (true)
                    {
                        Socket accept = ss.accept();
                        new Reader(accept, listeners);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
        this.start();
    }

    @Override
    public void run()
    {

        String[] list = mastList.split(";");
        for (int i = 0; i < list.length; i++)
        {
            try
            {
                int sport = Integer.parseInt(list[i]);
                Socket s = new Socket("localhost", sport);
                listeners.add(sport + "");
                // 向服务器推送端口
                new Writer(s, port, sport, listeners);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        while (true)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            String[] array = listeners.toArray(new String[] {});
            Arrays.sort(array);
            // System.out.println(Arrays.toString(array));
            System.out.println(Thread.currentThread().getName() + "投票给：" + array[0]);
        }
    }

}

class Reader extends Thread
{

    private Socket accept;
    private List<String> listeners;

    public Reader(Socket accept, List<String> listeners)
    {
        this.accept = accept;
        this.listeners = listeners;
        this.start();
    }

    @Override
    public void run()
    {
        String msg = null;
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            while ((msg = br.readLine()) != null)
            {
                if (!listeners.contains(msg))
                {
                    PrintWriter pw = new PrintWriter(accept.getOutputStream());
                    pw.println(msg);
                    listeners.add(msg);
                }
            }
        }
        catch (IOException e)
        {
            if (msg != null)
                listeners.remove(msg);
        }
    }

}

class Writer extends Thread
{

    private int port;
    private int sport;
    private Socket accept;
    private List<String> listeners;

    public Writer(Socket accept, int port, int sport, List<String> listeners)
    {
        this.accept = accept;
        this.port = port;
        this.sport = sport;
        this.listeners = listeners;
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
                Thread.sleep(500);
            }
        }
        catch (IOException | InterruptedException e)
        {
            listeners.remove(sport);
            System.out.println(port + "异常:" + e.getMessage());
        }
    }

}
