package com.dong.springcoud.demo.services.impl;

import org.springframework.web.bind.annotation.PathVariable;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.dong.springcoud.demo.services.QueryServices;

/**
 * 服务提供方
 *
 * @author zhdong
 * @version 1.0, 2018年3月22日
 * @since JDK1.8
 */
@HSFProvider(serviceInterface = QueryServices.class, serviceVersion = "1.0.0")
public class QueryServicesImpl implements QueryServices
{

    public String queryInfo(@PathVariable("path") String path)
    {

        System.out.println("进入远程查询服务");

        return "接收参数：[" + path + "]";
    }

}
