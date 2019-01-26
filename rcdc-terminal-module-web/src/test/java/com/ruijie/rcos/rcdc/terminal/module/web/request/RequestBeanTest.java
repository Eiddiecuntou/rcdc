package com.ruijie.rcos.rcdc.terminal.module.web.request;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.DetectPageWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.EditAdminPwdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.StartBatDetectWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdArrWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdDownLoadWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdWebRequest;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月25日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class RequestBeanTest {

    /**
     * 测试AddTerminalSystemUpgradeRequest
     */
    @Test
    public void testAddTerminalSystemUpgradeRequest() {
        GetSetTester tester = new GetSetTester(AddTerminalSystemUpgradeRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试CreateTerminalSystemUpgradeRequest
     */
    @Test
    public void testCreateTerminalSystemUpgradeRequest() {
        GetSetTester tester = new GetSetTester(CreateTerminalSystemUpgradeRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试DeleteTerminalSystemUpgradeRequest
     */
    @Test
    public void testDeleteTerminalSystemUpgradeRequest() {
        GetSetTester tester = new GetSetTester(DeleteTerminalSystemUpgradeRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试ListTerminalSystemUpgradePackageRequest
     */
    @Test
    public void testListTerminalSystemUpgradePackageRequest() {
        GetSetTester tester = new GetSetTester(ListTerminalSystemUpgradePackageRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    // ctrl/reauest包下的实体类
    
    /**
     * 测试DetectPageWebRequest
     */
    @Test
    public void testDetectPageWebRequest() {
        GetSetTester tester = new GetSetTester(DetectPageWebRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试EditAdminPwdWebRequest
     */
    @Test
    public void testEditAdminPwdWebRequest() {
        GetSetTester tester = new GetSetTester(EditAdminPwdWebRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试StartBatDetectWebRequest
     */
    @Test
    public void testStartBatDetectWebRequest() {
        GetSetTester tester = new GetSetTester(StartBatDetectWebRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试TerminalIdArrWebRequest
     */
    @Test
    public void testTerminalIdArrWebRequest() {
        GetSetTester tester = new GetSetTester(TerminalIdArrWebRequest.class);
        tester.runTest();
        
        String[] idArr = new String[1];
        TerminalIdArrWebRequest request = new TerminalIdArrWebRequest(idArr);
        assertArrayEquals(idArr, request.getIdArr());
    }
    
    /**
     * 测试TerminalIdDownLoadWebRequest
     */
    @Test
    public void testTerminalIdDownLoadWebRequest() {
        GetSetTester tester = new GetSetTester(TerminalIdDownLoadWebRequest.class);
        tester.runTest();
        assertTrue(true);
    }
    
    /**
     * 测试TerminalIdWebRequest
     */
    @Test
    public void testTerminalIdWebRequest() {
        GetSetTester tester = new GetSetTester(TerminalIdWebRequest.class);
        tester.runTest();
        assertTrue(true);
    }
}
