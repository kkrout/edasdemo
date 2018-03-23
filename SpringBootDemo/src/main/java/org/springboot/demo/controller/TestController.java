package org.springboot.demo.controller;

import org.springboot.demo.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController
{

    @Autowired
    private DemoMapper mapper;

    @RequestMapping(value = "/demo/test")
    public String demo()
    {
        return "进入demo";
    }

    @RequestMapping(value = "/demo/getMysqlData", method = RequestMethod.GET)
    public String getMysqlData()
    {
        return mapper.getMysqlData();
    }

}
