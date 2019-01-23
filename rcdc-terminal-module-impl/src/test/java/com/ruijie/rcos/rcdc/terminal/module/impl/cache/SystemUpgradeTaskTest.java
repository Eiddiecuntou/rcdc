package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class SystemUpgradeTaskTest {

    /**
     * 测试SetterAndGetter
     */
    @Test
    public void testSetterAndGetter() {
        GetSetTester tester = new GetSetTester(SystemUpgradeTask.class);
        tester.runTest();
        assertTrue(true);
    }

}
