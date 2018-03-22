package com.dong.springcloud.client;

import org.springframework.context.annotation.Configuration;

import com.alibaba.boot.hsf.annotation.HSFConsumer;
import com.dong.springcoud.demo.services.QueryServices;

@Configuration
public class HsfConfig
{
    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private QueryServices echoService;
}
