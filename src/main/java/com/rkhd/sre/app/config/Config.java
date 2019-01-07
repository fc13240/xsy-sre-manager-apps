package com.rkhd.sre.app.config;

import com.rkhd.sre.app.support.DefaultZookeeperConnectionManager;
import com.rkhd.sre.app.support.ZookeeperConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @ConditionalOnMissingBean
    @ConditionalOnClass(value = DefaultZookeeperConnectionManager.class)
    @Bean
    public ZookeeperConnectionManager createZookeeperConnectionManager() {
        return new DefaultZookeeperConnectionManager();
    }


}
