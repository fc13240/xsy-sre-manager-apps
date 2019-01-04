package com.rkhd.sre.app.support;

import com.google.common.collect.Maps;
import com.rkhd.sre.app.entity.ZookeeperInfo;
import com.rkhd.sre.app.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultZookeeperConnectionManager implements ZookeeperConnectionManager, DisposableBean {
    private static final Map<String, ZookeeperInfo> CONNECTION_MAP = Maps.newConcurrentMap();
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private CuratorFrameworkManager manager;

    @Override
    public ZookeeperInfo add(ZookeeperInfo zookeeperInfo) {
        if (zookeeperInfo != null && !StringUtils.isEmpty(zookeeperInfo.getConnectionString())) {
            String key = zookeeperInfo.getConnectionString().hashCode() + "";
            if (CONNECTION_MAP.containsKey(key)) {
                return CONNECTION_MAP.get(key);
            } else {
                zookeeperInfo.setId(key);
                manager.registerCuratorFramework(key, zookeeperInfo);
                return CONNECTION_MAP.put(key, zookeeperInfo);
            }
        }
        return new ZookeeperInfo();

    }

    @Override
    public ZookeeperInfo update(String id, ZookeeperInfo zookeeperInfo) {
        if (!StringUtils.isEmpty(id) && zookeeperInfo != null && !StringUtils.isEmpty(zookeeperInfo.getConnectionString())) {
            String key = zookeeperInfo.getConnectionString().hashCode() + "";
            manager.removeCuratorFramework(id);
            manager.registerCuratorFramework(key, zookeeperInfo);
            zookeeperInfo.setId(key);
            CONNECTION_MAP.remove(id);
            return CONNECTION_MAP.put(key, zookeeperInfo);
        }
        return new ZookeeperInfo();
    }

    @Override
    public ZookeeperInfo delete(String id) {
        manager.removeCuratorFramework(id);
        return CONNECTION_MAP.remove(id);
    }

    @Override
    public List<ZookeeperInfo> findAll() {
        return CONNECTION_MAP.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public ZookeeperInfo getByKey(String id) {
        return CONNECTION_MAP.get(id);
    }

    @Override
    public void reset(String id) {
        ZookeeperInfo zookeeperInfo = CONNECTION_MAP.get(id);
        if (zookeeperInfo != null) {
            manager.registerCuratorFramework(id, zookeeperInfo);
        }
    }

    @Override
    public void destroy() throws Exception {

    }
}
