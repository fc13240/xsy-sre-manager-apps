package com.rkhd.sre.app.support.zookeeper;

import com.rkhd.sre.app.entity.ZookeeperInfo;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

public class RedisZookeeperConnectionManager implements ZookeeperConnectionManager, DisposableBean {
    @Override
    public ZookeeperInfo add(ZookeeperInfo zookeeperInfo) {
        return null;
    }

    @Override
    public ZookeeperInfo update(String id, ZookeeperInfo zookeeperInfo) {
        return null;
    }

    @Override
    public ZookeeperInfo delete(String id) {
        return null;
    }

    @Override
    public List<ZookeeperInfo> findAll() {
        return null;
    }

    @Override
    public ZookeeperInfo getByKey(String id) {
        return null;
    }

    @Override
    public void reset(String id) {

    }

    @Override
    public void fixMap() {

    }

    @Override
    public void destroy() throws Exception {

    }
}
