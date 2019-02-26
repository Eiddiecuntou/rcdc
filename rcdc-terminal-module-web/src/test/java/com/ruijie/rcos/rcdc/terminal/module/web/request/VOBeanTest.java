package com.ruijie.rcos.rcdc.terminal.module.web.request;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.CreateSystemUpgradeTaskContentVO;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.TerminalDetectListContentVO;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class VOBeanTest {
    
    /**
     * 测试CreateSystemUpgradeTaskContentVO
     */
    @Test
    public void testCreateSystemUpgradeTaskContentVO() {
        GetSetTester tester = new GetSetTester(CreateSystemUpgradeTaskContentVO.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试TerminalDetectListContentVO
     */
    @Test
    public void testTerminalDetectListContentVO() {
        GetSetTester tester = new GetSetTester(TerminalDetectListContentVO.class);
        tester.runTest();
        assertTrue(true);
    }

}
