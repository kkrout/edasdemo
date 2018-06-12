package com.dong.springcoud.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.taobao.pandora.boot.PandoraBootstrap;

/**
 * 启动类入口
 *
 * @author zhdong
 * @version 1.0, 2018年3月22日
 * @since JDK1.8
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServerApplication
{
    public static void main(String[] args)
    {
        PandoraBootstrap.run(args);
        SpringApplication.run(ServerApplication.class, args);
        PandoraBootstrap.markStartupAndWait();
    }
}
