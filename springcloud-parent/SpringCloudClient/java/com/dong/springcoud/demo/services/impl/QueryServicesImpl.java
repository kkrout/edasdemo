package com.dong.springcoud.demo.services.impl;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryServicesImpl
{

    @RequestMapping(value = "/query/{path}", method = RequestMethod.GET)
    public String queryInfo(@PathVariable String path)
    {

        return "从数据库查询得到数据";
    }

}
