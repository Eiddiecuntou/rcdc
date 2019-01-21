package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class SystemUpgradeTaskComparatorTest {

    @Tested
    private SystemUpgradeTaskComparator comparator;

    /**
     * 测试比较
     */
    @Test
    public void testCompare() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        o2.setStartTime(curTime - 1);

        int compare = comparator.compare(o1, o2);
        Assert.assertEquals(compare, -1);
    }

    /**
     * 测试比较-第一个参数较小
     */
    @Test
    public void testCompareStateEqualFirstTimeSmall() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        o2.setStartTime(curTime + 1);

        int compare = comparator.compare(o1, o2);
        Assert.assertEquals(compare, 1);
    }

    /**
     * 测试比较-状态不同
     */
    @Test
    public void testCompareStateNotEqual() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.WAIT);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        o2.setStartTime(curTime - 1);

        int compare = comparator.compare(o1, o2);
        Assert.assertEquals(compare, -1);
    }

    /**
     * 测试状态不同
     */
    @Test
    public void testCompareStateNotEqualOther() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.WAIT);
        o2.setStartTime(curTime - 1);

        int compare = comparator.compare(o1, o2);

        Assert.assertEquals(compare, 1);
    }
}
