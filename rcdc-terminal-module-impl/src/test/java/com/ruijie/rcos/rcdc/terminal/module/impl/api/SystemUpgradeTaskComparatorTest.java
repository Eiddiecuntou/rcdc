package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.Date;
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

    @Test
    public void testCompare() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.DOING);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.DOING);
        o2.setStartTime(curTime - 1);
        
        comparator.compare(o1, o2);
    }
    
    @Test
    public void testCompareStateEqualFirstTimeSmall() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.DOING);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.DOING);
        o2.setStartTime(curTime + 1);
        
        comparator.compare(o1, o2);
    }

    @Test
    public void testCompareStateNotEqual() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.WAIT);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.DOING);
        o2.setStartTime(curTime - 1);
        
        comparator.compare(o1, o2);
    }
    
    @Test
    public void testCompareStateNotEqualOther() {
        long curTime = new Date().getTime();
        SystemUpgradeTask o1 = new SystemUpgradeTask();
        o1.setState(CbbSystemUpgradeStateEnums.DOING);
        o1.setStartTime(curTime);
        SystemUpgradeTask o2 = new SystemUpgradeTask();
        o2.setState(CbbSystemUpgradeStateEnums.WAIT);
        o2.setStartTime(curTime - 1);
        
        comparator.compare(o1, o2);
    }
}
