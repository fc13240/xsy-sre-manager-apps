package com.rkhd.sre.app.support;

import com.rkhd.sre.app.entity.ZookeeperInfo;

import java.util.List;

public interface ZookeeperConnectionManager {
    public ZookeeperInfo add(ZookeeperInfo zookeeperInfo);

    public ZookeeperInfo update(String id,ZookeeperInfo zookeeperInfo);

    public ZookeeperInfo delete(String id);

    public List<ZookeeperInfo> findAll();


    public ZookeeperInfo getByKey(String id);

    public void reset(String id);
}
