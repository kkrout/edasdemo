package org.springboot.demo;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.spring.boot.starter.SpringBootVFS;

@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisPlusConfig
{
    @Autowired
    private PropertyResolver environment;
    private RelaxedPropertyResolver propertyResolver;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private MybatisProperties properties;
    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Autowired(required = false)
    private Interceptor[] interceptors;
    @Autowired(required = false)
    private DatabaseIdProvider databaseIdProvider;

    /**
     * 配置DataSource
     * 
     * @return
     * @throws SQLException
     */
    @Bean
    public DataSource druidDataSource() throws SQLException
    {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");

        System.out.println("====================注入druid!====================");
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(propertyResolver.getProperty("url"));
        datasource.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
        datasource.setUsername(propertyResolver.getProperty("username"));
        datasource.setPassword(propertyResolver.getProperty("password"));
        datasource.setInitialSize(Integer.valueOf(propertyResolver.getProperty("initial-size")));
        datasource.setMinIdle(Integer.valueOf(propertyResolver.getProperty("min-idle")));
        datasource.setMaxWait(Long.valueOf(propertyResolver.getProperty("max-wait")));
        datasource.setMaxActive(Integer.valueOf(propertyResolver.getProperty("max-active")));
        datasource.setMinEvictableIdleTimeMillis(
                Long.valueOf(propertyResolver.getProperty("min-evictable-idle-time-millis")));
        try
        {
            datasource.setFilters(propertyResolver.getProperty("filters"));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return datasource;
    }

    /**
     * @Description : mybatis-plus分页插件
     *              ---------------------------------
     * @Author : Liang.Guangqing
     * @Date : Create in 2017/9/19 13:59
     */
    @Bean
    public PaginationInterceptor paginationInterceptor()
    {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        return page;
    }

    /**
     * 这里全部使用mybatis-autoconfigure 已经自动加载的资源。不手动指定
     * 配置文件和mybatis-boot的配置文件同步
     * 
     * @return
     */
    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean()
    {
        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        mybatisPlus.setDataSource(dataSource);
        mybatisPlus.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation()))
        {
            mybatisPlus.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        mybatisPlus.setConfiguration(properties.getConfiguration());
        if (!ObjectUtils.isEmpty(this.interceptors))
        {
            mybatisPlus.setPlugins(this.interceptors);
        }
        // MP 全局配置，更多内容进入类看注释
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.setDbType(DBType.MYSQL.name());
        // ID 策略 AUTO->`0`("数据库ID自增") INPUT->`1`(用户输入ID") ID_WORKER->`2`("全局唯一ID")
        // UUID->`3`("全局唯一ID")
        globalConfig.setIdType(2);
        mybatisPlus.setGlobalConfig(globalConfig);
        MybatisConfiguration mc = new MybatisConfiguration();
        mc.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisPlus.setConfiguration(mc);
        if (this.databaseIdProvider != null)
        {
            mybatisPlus.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage()))
        {
            mybatisPlus.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage()))
        {
            mybatisPlus.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations()))
        {
            mybatisPlus.setMapperLocations(this.properties.resolveMapperLocations());
        }
        return mybatisPlus;
    }

}