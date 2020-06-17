package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Mocked;
import mockit.Tested;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年06月17日
 *
 * @author xgx
 */
@RunWith(SkyEngineRunner.class)
public class UpgradeTerminalLockManagerTest {
    @Tested
    private UpgradeTerminalLockManager upgradeTerminalLockManager;


    /**
     * 测试GetAndCreateLock方法
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetAndCreateLock() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeTerminalLockManager.getAndCreateLock(null), "terminalId can not be empty");
        UUID terminalId = UUID.randomUUID();
        Lock reentrantLock = upgradeTerminalLockManager.getAndCreateLock(terminalId.toString());
        Map<String, Lock> terminalLockMap = (Map<String, Lock>) FieldUtils.readField(upgradeTerminalLockManager, "terminalLockMap", true);

        Assert.assertEquals(reentrantLock, terminalLockMap.get(terminalId.toString()));

    }
}
