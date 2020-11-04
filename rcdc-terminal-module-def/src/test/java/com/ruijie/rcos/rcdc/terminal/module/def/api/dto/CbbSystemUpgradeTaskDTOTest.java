package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SkyEngineRunner.class)
public class CbbSystemUpgradeTaskDTOTest {

    /**
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        GetSetTester getSetTester = new GetSetTester(CbbSystemUpgradeTaskDTO .class);
        getSetTester.runTest();
        Assert.assertTrue(true);
    }
}