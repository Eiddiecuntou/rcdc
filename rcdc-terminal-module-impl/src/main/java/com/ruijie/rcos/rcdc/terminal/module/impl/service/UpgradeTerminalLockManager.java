package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 终端升级操作锁管理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/5/10
 *
 * @author nt
 */
@Service
public class UpgradeTerminalLockManager {

    private Map<String, Lock> terminalLockMap = Maps.newConcurrentMap();

    /**
     * 获取升级终端操作锁
     *
     * @param terminalId 终端id
     * @return 终端操作锁
     */
    public synchronized Lock getAndCreateLock(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be empty");

        return terminalLockMap.computeIfAbsent(terminalId, k -> new ReentrantLock());
    }




}
