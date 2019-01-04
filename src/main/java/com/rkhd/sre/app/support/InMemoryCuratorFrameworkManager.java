package com.rkhd.sre.app.support;

import com.google.common.collect.Maps;
import com.rkhd.sre.app.entity.ZookeeperInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryOneTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class InMemoryCuratorFrameworkManager implements CuratorFrameworkManager, DisposableBean {

    private static final Map<String, CuratorFramework> CURATOR_FRAMEWORK_MAP = Maps.newConcurrentMap();
    private static final Map<String, Integer> KEY_COUNT = Maps.newConcurrentMap();
    private static ExecutorService service = Executors.newCachedThreadPool();
    private static ExecutorService regist = Executors.newCachedThreadPool();


    @Autowired
    private ZookeeperConnectionManager zookeeperConnectionManager;

    private static final RetryPolicy RETRY_POLICY = new RetryOneTime(1);

    class RegisterBuild {
        String id;
        ZookeeperInfo zookeeperInfo;

        public RegisterBuild(String id, ZookeeperInfo zookeeperInfo) {
            this.id = id;
            this.zookeeperInfo = zookeeperInfo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ZookeeperInfo getZookeeperInfo() {
            return zookeeperInfo;
        }

        public void setZookeeperInfo(ZookeeperInfo zookeeperInfo) {
            this.zookeeperInfo = zookeeperInfo;
        }
    }

    final ValueSynLock<String, RegisterBuild> registerLock = new ValueSynLock<String, RegisterBuild>() {


        @Override
        public void run(RegisterBuild registerBuild) {
            register(registerBuild);
        }
    };

    @Override
    public void registerCuratorFramework(String id, ZookeeperInfo zookeeperInfo) {
        RegisterBuild registerBuild = new RegisterBuild(id, zookeeperInfo);
        regist.execute(new Runnable() {
            @Override
            public void run() {
                registerLock.startWork(id, registerBuild);
            }
        });
    }

    public void register(RegisterBuild registerBuild) {
        String id = registerBuild.getId();
        ZookeeperInfo zookeeperInfo = registerBuild.getZookeeperInfo();
        try {
            if (CURATOR_FRAMEWORK_MAP.containsKey(id)) {
                removeCuratorFramework(id);
            }
            CuratorFramework curatorFramework = createAndStartCuratorFramework(zookeeperInfo.getConnectionString(), zookeeperInfo.getSessionTimeout());
            CURATOR_FRAMEWORK_MAP.put(id, curatorFramework);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private CuratorFramework createAndStartCuratorFramework(String connectionString, Integer sessionTimeout) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(3000)
                .retryPolicy(RETRY_POLICY)
                .maxCloseWaitMs(3000)
                .build();
        log.info("curatorFramework will start");
        curatorFramework.start();
        curatorFramework.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            @Override
            public void unhandledError(String s, Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                String key = curatorFramework.getZookeeperClient().getCurrentConnectionString().hashCode() + "";
                if (connectionState.equals(ConnectionState.CONNECTED)) {
                    KEY_COUNT.remove(key);
                }
                if (!StringUtils.isEmpty(key)) {
                    ZookeeperInfo zk = zookeeperConnectionManager.getByKey(key);
                    if (zk != null) {
                        zk.setConnectionState(connectionState);
                    }
                }
            }
        });
        return curatorFramework;
    }

    @Override
    public CuratorFramework getCuratorFramework(String id) {
        return CURATOR_FRAMEWORK_MAP.get(id);
    }

    @Override
    public void removeCuratorFramework(String id) {
        CuratorFramework curatorFramework = CURATOR_FRAMEWORK_MAP.get(id);
        if (null != curatorFramework) {
            curatorFramework.close();
            log.info("删除");
        }
        CURATOR_FRAMEWORK_MAP.remove(id);
    }

    final ValueSynLock<String, String> fixLock = new ValueSynLock<String, String>() {

        @Override
        public void run(String s) {
            removeCuratorFramework(s);
            KEY_COUNT.remove(s);
        }
    };

    /**
     * 删除非connect状态扫描超过三次的连接
     */
    @Override
    public void fixMap() {


        Set<Map.Entry<String, CuratorFramework>> keys = CURATOR_FRAMEWORK_MAP.entrySet();
        Set<String> deletekeys = keys.stream().filter(entry -> !ConnectionState.CONNECTED.equals(entry.getValue().getState())).map(Map.Entry::getKey).collect(Collectors.toSet());
        deletekeys.stream().forEach(key -> {
            int count = KEY_COUNT.computeIfAbsent(key, s -> 0);
            KEY_COUNT.computeIfPresent(key, (k, v) -> v + 1);

        });

        KEY_COUNT.entrySet().stream().filter(s -> s.getValue() >= 3)
                .forEach(entity -> {
                    zookeeperConnectionManager.getByKey(entity.getKey()).setConnectionState(ConnectionState.LOST);
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            fixLock.startWork(entity.getKey(), entity.getKey());
                        }
                    });
                });
    }

    @Override
    public void destroy() {
        CURATOR_FRAMEWORK_MAP.values().forEach(
                each -> {
                    try {
                        each.close();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }
}
