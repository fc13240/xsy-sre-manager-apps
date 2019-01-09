package com.rkhd.sre.app.support.discovery;

import com.google.common.collect.Maps;
import com.rkhd.sre.app.entity.DiscoveryConnectionInfo;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultDiscoveryConnectionManager implements DiscoveryConnectionManager, DisposableBean {

    private static final Map<String, DiscoveryConnectionInfo> CONNECTION_MAP = Maps.newConcurrentMap();


    @Override
    public DiscoveryConnectionInfo add(DiscoveryConnectionInfo discoveryConnectionInfo) {
        if (discoveryConnectionInfo != null && !StringUtils.isEmpty(discoveryConnectionInfo.getConnectionString())) {
            String key = discoveryConnectionInfo.getConnectionString().hashCode() + "";
            if (CONNECTION_MAP.containsKey(key)) {
                return CONNECTION_MAP.get(key);
            } else {
                discoveryConnectionInfo.setId(key);
                return CONNECTION_MAP.put(key, discoveryConnectionInfo);
            }
        }
        return new DiscoveryConnectionInfo();
    }

    @Override
    public DiscoveryConnectionInfo update(String id, DiscoveryConnectionInfo discoveryConnectionInfo) {
        if (!StringUtils.isEmpty(id) && discoveryConnectionInfo != null && !StringUtils.isEmpty(discoveryConnectionInfo.getConnectionString())) {
            String key = discoveryConnectionInfo.getConnectionString().hashCode() + "";
            discoveryConnectionInfo.setId(key);
            CONNECTION_MAP.remove(id);
            return CONNECTION_MAP.put(key, discoveryConnectionInfo);
        }
        return new DiscoveryConnectionInfo();
    }

    @Override
    public DiscoveryConnectionInfo delete(String id) {
        return CONNECTION_MAP.remove(id);
    }

    @Override
    public List<DiscoveryConnectionInfo> findAll() {
        return CONNECTION_MAP.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public DiscoveryConnectionInfo getByKey(String id) {
        return CONNECTION_MAP.get(id);
    }



    @Override
    public void fixMap() {

    }

    @Override
    public void destroy() throws Exception {

    }
}
