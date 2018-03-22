package com.dong.springcloud.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dong.springcoud.demo.services.QueryServices;

@RestController
public class TestController
{
    @Autowired
    private QueryServices echoService;

    /**
     * 通过服务发现机制调用
     * 
     * @author zhdong
     * @param str
     * @return
     */
    @RequestMapping(value = "/test/{str}", method = RequestMethod.GET)
    public String feign(@PathVariable("str") String str)
    {
        return echoService.queryInfo(str);
    }

}
