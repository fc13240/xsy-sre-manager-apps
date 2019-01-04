package com.rkhd.sre.app.task;

import com.rkhd.sre.app.support.CuratorFrameworkManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledService {
    @Autowired
    private CuratorFrameworkManager manager;

    @Scheduled(cron = "0/15 * * * * ?  ")
    public void task() {
        log.info(">>>>>>>任务开始<<<<<<<<");
        manager.fixMap();
        log.info(">>>>>>>任务结束<<<<<<<<");
    }
}