package com.rkhd.sre.app.task;

import com.rkhd.sre.app.support.zookeeper.ZookeeperConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledService {
    @Autowired
    private ZookeeperConnectionManager manager;

    @Scheduled(cron = "0/15 * * * * ?  ")
    public void task() {
        log.debug(">>>>>>>任务开始<<<<<<<<");
        manager.fixMap();
        log.debug(">>>>>>>任务结束<<<<<<<<");
    }
}
