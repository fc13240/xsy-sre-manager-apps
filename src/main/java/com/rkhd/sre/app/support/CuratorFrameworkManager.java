package com.rkhd.sre.app.support;

import com.rkhd.sre.app.entity.ZookeeperInfo;
import org.apache.curator.framework.CuratorFramework;

import java.util.Set;


public interface CuratorFrameworkManager {

	void registerCuratorFramework(String id, ZookeeperInfo zookeeperInfo);

	CuratorFramework getCuratorFramework(String id);

	void removeCuratorFramework(String id);

	void fixMap();

	void destroy();

	void stop(Set<String> ids);
}
