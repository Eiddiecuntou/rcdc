package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class SystemUpgradeGlobalTest {

    /**
     * 测试获取升级位置 - 已有位置
     */
    @Test
    public void testCheckAndHoldUpgradeQuotaHasQuota() {
        String terminalId = "123";

        new MockUp<HashSet>() {
            @Mock
            public boolean contains(Object obj) {
                return true;
            }

        };
        boolean canUpgrade = SystemUpgradeGlobal.checkAndHoldUpgradeQuota(terminalId);
        assertTrue(canUpgrade);
    }

    /**
     * 测试获取升级位置 - 升级位置已满
     */
    @Test
    public void testCheckAndHoldUpgradeQuotaNotLessThanMaxSize() {
        String terminalId = "123";

        new MockUp<HashSet>() {
            @Mock
            public boolean contains(Object obj) {
                return false;
            }

            @Mock
            public int size() {
                return 60;
            }

        };

        boolean canUpgrade = SystemUpgradeGlobal.checkAndHoldUpgradeQuota(terminalId);
        assertTrue(!canUpgrade);
    }

    /**
     * 测试获取升级位置
     */
    @Test
    public void testCheckAndHoldUpgradeQuota() {
        String terminalId = "123";

        new MockUp<HashSet>() {
            @Mock
            public boolean contains(Object obj) {
                return false;
            }

            @Mock
            public int size() {
                return 59;
            }

        };

        boolean canUpgrade = SystemUpgradeGlobal.checkAndHoldUpgradeQuota(terminalId);
        assertTrue(canUpgrade);
    }

    /**
     * 测试是否升级位置
     */
    @Test
    public void testReleaseUpgradeQuota() {
        Set<String> set = (HashSet) Deencapsulation.getField(SystemUpgradeGlobal.class, "UPGRADING_TERMINAL_SET");
        set.add("123");

        try {
            assertEquals(1, set.size());

            SystemUpgradeGlobal.releaseUpgradeQuota("123");

            assertEquals(0, set.size());

        } finally {
            set.clear();
        }
    }

    /**
     * 测试是否升级位置 - 结果为：是
     */
    @Test
    public void testIsUpgradingNumExceedLimitTrue() {
        Set<String> set = (HashSet) Deencapsulation.getField(SystemUpgradeGlobal.class, "UPGRADING_TERMINAL_SET");
        for (int i = 0; i < 60; i++) {
            set.add(String.valueOf(i));
        }

        try {
            assertEquals(60, set.size());

            boolean isExceedLimit = SystemUpgradeGlobal.isUpgradingNumExceedLimit();

            assertTrue(isExceedLimit);

        } finally {
            set.clear();
        }
    }

    /**
     * 测试是否升级位置 - 结果为：否
     */
    @Test
    public void testIsUpgradingNumExceedLimitFalse() {
        Set<String> set = (HashSet) Deencapsulation.getField(SystemUpgradeGlobal.class, "UPGRADING_TERMINAL_SET");
        for (int i = 0; i < 59; i++) {
            set.add(String.valueOf(i));
        }

        try {
            assertEquals(59, set.size());

            boolean isExceedLimit = SystemUpgradeGlobal.isUpgradingNumExceedLimit();

            assertTrue(!isExceedLimit);

        } finally {
            set.clear();
        }
    }
}
