package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class ModelBeanTest {

    /**
     * 测试FilePropertyInfo
     */
    @Test
    public void testFilePropertyInfo() {
        GetSetTester tester = new GetSetTester(FilePropertyInfo.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalSystemUpgradeInfo
     */
    @Test
    public void testTerminalSystemUpgradeInfo() {
        GetSetTester tester = new GetSetTester(TerminalSystemUpgradeInfo.class);
        tester.runTest();
        TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
        upgradeInfo.setTerminalId("1");
        upgradeInfo.setState(CbbSystemUpgradeStateEnums.SUCCESS);

        String string = "TerminalSystemUpgradeInfo [terminalId=1, state=" + CbbSystemUpgradeStateEnums.SUCCESS + "]";
        assertEquals(string, upgradeInfo.toString());
    }

    /**
     * 测试TerminalUpgradeVersionFileInfo
     */
    @Test
    public void testTerminalUpgradeVersionFileInfo() {
        GetSetTester tester = new GetSetTester(TerminalUpgradeVersionFileInfo.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalVersionResultDTO
     */
    @Test
    public void testTerminalVersionResultDTO() {
        GetSetTester tester = new GetSetTester(TerminalVersionResultDTO.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试SambaInfoDTO
     */
    @Test
    public void testSambaInfoDTO() {
        GetSetTester tester = new GetSetTester(SambaInfoDTO.class);
        tester.runTest();
        assertTrue(true);
    }
}
