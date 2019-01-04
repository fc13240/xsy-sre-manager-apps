package com.rkhd.sre.app.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

public abstract class ValueSynLock<T, K> {


    /**
     * @Fields signMap : TODO(存储一个同步信号量，key表示同步的值)
     */
    private static ConcurrentMap<Object, Semaphore> signMap = new ConcurrentHashMap<Object, Semaphore>();

    /**
     * 要执行的方法
     */
    public abstract void run(K k);

    /**
     * 开始执行操作
     *
     * @param value
     */
    public void startWork(T value, K param) {
        Semaphore se = putMapAndGetSemaphore(value);
        try {
            se.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        run(param);
        releaseLockMap(value);
        se.release();
    }

    /**
     * 释放map
     *
     * @param
     */
    public void releaseLockMap(T a) {
        Semaphore se = signMap.get(a);
        if (se != null) {
            signMap.remove(a);
        }
    }

    /**
     * @value 放入同步值，获取同步信号量
     * @return
     */
    public Semaphore putMapAndGetSemaphore(T value) {
        Semaphore se = signMap.get(value);
        if (se == null) {
            se = new Semaphore(1);
            signMap.put(value, se);
        }
        return signMap.get(value);
    }
}