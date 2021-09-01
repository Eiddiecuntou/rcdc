package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;


import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * Description: 终端lock帮助类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/1
 *
 * @author zhiweiHong
 */
@Service
public class TerminalLockHelper {

    private static final LoadingCache<String, Lock> TERMINAL_LOCK_CACHE =
            CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, Lock>() {
                @Override
                public Lock load(String uuid) throws Exception {
                    return new ReentrantLock();
                }
            });

    /**
     * 添加并且获取锁
     * 
     * @param terminalId 终端Id
     * @return 锁
     */
    public synchronized Lock putAndGetLock(String terminalId) {
        Assert.notNull(terminalId, "terminal can not be null");

        Lock terminalLock = TERMINAL_LOCK_CACHE.getIfPresent(terminalId);
        if (Objects.nonNull(terminalLock)) {
            return terminalLock;
        }

        Lock lock = new ReentrantLock();
        TERMINAL_LOCK_CACHE.put(terminalId, lock);
        return lock;
    }


    /**
     * 将缓存中数据作废
     * 
     * @param terminalId 终端Id
     */
    public synchronized void invalidate(String terminalId) {
        Assert.notNull(terminalId, "terminal can not be null");

        Lock terminalLock = TERMINAL_LOCK_CACHE.getIfPresent(terminalId);
        if (Objects.nonNull(terminalLock)) {
            TERMINAL_LOCK_CACHE.invalidate(terminalId);
        }
    }

}
