package org.springboot.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test
{

    public static void main(String[] args) throws Exception
    {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(200);
        httpsRequest("http://172.16.80.175:8080/ECW-Mall-WXMP/mp/share/expireAccessToken.ihtml?token=123213", "GET",
                null);
        for (int i = 0; i < 100; i++)
        {
            Runnable task = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        // httpsRequest("http://localhost:8080/ECW-Mall-WXMP/mp/share/goableAccessToken.ihtml?token=111",
                        // "GET", null);
                        httpsRequest(
                                "http://172.16.80.175:8080/ECW-Mall-WXMP/mp/share/goableAccessToken.ihtml?token=111",
                                "GET", null);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            newFixedThreadPool.execute(task);
        }
        newFixedThreadPool.shutdown();
    }

    public static void httpsRequest(String requestUrl, String requestMethod, String outputStr) throws Exception
    {

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置请求方式（GET/POST）
        conn.setRequestMethod(requestMethod);

        // 当outputStr不为null时向输出流写数据
        if (null != outputStr)
        {
            OutputStream outputStream = conn.getOutputStream();
            // 注意编码格式
            outputStream.write(outputStr.getBytes("UTF-8"));
            outputStream.close();
        }

        // 从输入流读取返回内容
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null)
        {
            buffer.append(str);
        }

        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        conn.disconnect();
        System.out.println(buffer.toString());
    }
}
