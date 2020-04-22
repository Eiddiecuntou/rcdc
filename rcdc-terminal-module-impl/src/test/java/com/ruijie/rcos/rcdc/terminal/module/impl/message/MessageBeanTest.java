package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class MessageBeanTest {

    /**
     * 测试ChangeHostNameRequest
     */
    @Test
    public void testChangeHostNameRequest() {
        ChangeHostNameRequest request = new ChangeHostNameRequest("123");
        assertEquals("123", request.getHostName());
        request.setHostName("456");
        assertEquals("456", request.getHostName());
    }

    /**
     * 测试ChangeTerminalPasswordRequest
     */
    @Test
    public void testChangeTerminalPasswordRequest() {
        GetSetTester tester = new GetSetTester(ChangeTerminalPasswordRequest.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试ShineNetworkConfig
     */
    @Test
    public void testShineNetworkConfig() {
        GetSetTester tester = new GetSetTester(ShineNetworkConfig.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试ShineTerminalBasicInfo
     */
    @Test
    public void testShineTerminalBasicInfo() {
        GetSetTester tester = new GetSetTester(CbbShineTerminalBasicInfo.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalDetectResponse
     */
    @Test
    public void testTerminalDetectResponse() {
        GetSetTester tester = new GetSetTester(TerminalDetectResponse.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalDetectResult
     */
    @Test
    public void testTerminalDetectResult() {
        GetSetTester tester = new GetSetTester(TerminalDetectResult.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试TerminalSystemUpgradeMsg
     */
    @Test
    public void testTerminalSystemUpgradeMsg() {
        GetSetTester tester = new GetSetTester(TerminalSystemUpgradeMsg.class);
        tester.runTest();
        assertTrue(true);
    }

    /**
     * 测试SystemUpgradeResultInfo
     */
    @Test
    public void testSystemUpgradeResultInfo() {
        GetSetTester tester = new GetSetTester(SystemUpgradeResultInfo.class);
        tester.runTest();
        assertTrue(true);
    }
}
