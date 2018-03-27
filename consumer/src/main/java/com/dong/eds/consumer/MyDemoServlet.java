package com.dong.eds.consumer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dong.edas.services.DemoServices;

public class MyDemoServlet extends HttpServlet
{

    /**
     * 序列版本
     * 
     * @author zhdong
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");
        DemoServices demo = (DemoServices) WebApplicationContextUtils.getWebApplicationContext(req.getServletContext())
                .getBean("demoServices");
        long s = System.nanoTime();
        String messsage = demo.getMesssage("我传什么你就给我什么");
        System.out.println("耗时：" + (System.nanoTime() - s));
        resp.getWriter().print(messsage);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

}
