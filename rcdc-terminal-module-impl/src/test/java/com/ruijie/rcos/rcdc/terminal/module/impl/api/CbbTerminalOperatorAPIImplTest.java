package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CbbTerminalOperatorAPIImplTest {

    @Tested
    private CbbTerminalOperatorAPIImpl terminalOperatorAPI;

    @Injectable
    private TerminalOperatorService operatorService;

    @Injectable
    private GatherLogCacheManager gatherLogCacheManager;

    @Test
    public void testShutdown() throws BusinessException {

        try {
            String terminalId = "123";
            terminalOperatorAPI.shutdown(terminalId);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {{
            operatorService.shutdown(anyString);
            times = 1;
        }};

    }

    @Test
    public void testRestart() throws BusinessException {
        try {
            String terminalId = "123";
            terminalOperatorAPI.restart(terminalId);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {{
            operatorService.restart(anyString);
            times = 1;
        }};
    }

    @Test
    public void testChangePassword() throws BusinessException {
        try {
            String terminalId = "123";
            String password = "adf";
            terminalOperatorAPI.changePassword(terminalId, password);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {{
            operatorService.changePassword(anyString, anyString);
            times = 1;
        }};

    }

    @Test
    public void testGatherLog() throws BusinessException {
        try {
            String terminalId = "123";
            terminalOperatorAPI.gatherLog(terminalId);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {{
            operatorService.gatherLog(anyString);
            times = 1;
        }};
    }

    @Test
    public void testDetect() throws BusinessException {
        try {
            String terminalId = "123";
            terminalOperatorAPI.detect(terminalId);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {{
            operatorService.detect(anyString);
            times = 1;
        }};
    }

    @Test
    public void testDetectForArr() throws BusinessException {
        String[] terminalIdArr = {"1", "2", "3"};

        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            public void detect(String terminalId) throws BusinessException {
            }
        };

        try {
            terminalOperatorAPI.detect(terminalIdArr);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {{
            terminalOperatorAPI.detect(anyString);
            times = 3;
        }};

    }

    @Test
    public void testGetTerminalLogNameIsNull() {
        new Expectations() {{
            gatherLogCacheManager.getCache(anyString);
            result = null;
        }};
        String terminalId = "123";
        try {
            terminalOperatorAPI.getTerminalLogName(terminalId);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
    }

    @Test
    public void testGetTerminalLogNameStateIsFailure() {
        GatherLogCache cache = new GatherLogCache();
        cache.setState(GatherLogStateEnums.FAILURE);
        new Expectations() {{
            gatherLogCacheManager.getCache(anyString);
            result = cache;
        }};
        String terminalId = "123";
        try {
            terminalOperatorAPI.getTerminalLogName(terminalId);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
    }

    @Test
    public void testGetTerminalLogName() {
        String logName = "shine.zip";
        GatherLogCache cache = new GatherLogCache();
        cache.setState(GatherLogStateEnums.DONE);
        cache.setLogFileName(logName);
        new Expectations() {{
            gatherLogCacheManager.getCache(anyString);
            result = cache;
        }};
        String terminalId = "123";
        try {
            String result = terminalOperatorAPI.getTerminalLogName(terminalId);
            Assert.assertEquals(result, logName);

        } catch (BusinessException e) {
            fail();
        }
    }
}