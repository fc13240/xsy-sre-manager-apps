package com.rkhd.sre.app.config;

import com.rkhd.sre.app.support.discovery.DefaultDiscoveryConnectionManager;
import com.rkhd.sre.app.support.discovery.DiscoveryConnectionManager;
import com.rkhd.sre.app.support.zookeeper.DefaultZookeeperConnectionManager;
import com.rkhd.sre.app.support.zookeeper.ZookeeperConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @ConditionalOnMissingBean
    @ConditionalOnProperty(name="zkManager",havingValue = "DefaultZookeeperConnectionManager")
    @ConditionalOnClass(value = DefaultZookeeperConnectionManager.class)
    @Bean
    public ZookeeperConnectionManager createZookeeperConnectionManager() {
        return new DefaultZookeeperConnectionManager();
    }


    @ConditionalOnMissingBean
    @ConditionalOnProperty(name="discoveryManager",havingValue = "DefaultDiscoveryConnectionManager")
    @ConditionalOnClass(value = DefaultZookeeperConnectionManager.class)
    @Bean
    public DiscoveryConnectionManager createDiscoveryConnectionManager() {
        return new DefaultDiscoveryConnectionManager();
    }


}
