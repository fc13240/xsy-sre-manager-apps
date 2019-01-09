package com.rkhd.sre.app.support.discovery;

import com.rkhd.sre.app.entity.DiscoveryConnectionInfo;

import java.util.List;

public interface DiscoveryConnectionManager {

    public DiscoveryConnectionInfo add(DiscoveryConnectionInfo discoveryConnectionInfo);

    public DiscoveryConnectionInfo update(String id, DiscoveryConnectionInfo discoveryConnectionInfo);

    public DiscoveryConnectionInfo delete(String id);

    public List<DiscoveryConnectionInfo> findAll();

    public DiscoveryConnectionInfo getByKey(String id);

    public void fixMap();
}
