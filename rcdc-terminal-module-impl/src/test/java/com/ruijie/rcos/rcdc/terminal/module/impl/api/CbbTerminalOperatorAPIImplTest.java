package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.fail;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBatDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.*;
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
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.shutdown(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                operatorService.shutdown(anyString);
                times = 1;
            }
        };

    }

    @Test
    public void testRestart() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.shutdown(request);
            terminalOperatorAPI.restart(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.restart(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testChangePassword() throws BusinessException {
        try {
            String terminalId = "123";
            String password = "adf";
            CbbChangePasswordRequest request = new CbbChangePasswordRequest();
            request.setTerminalId(terminalId);
            request.setPassword(password);
            terminalOperatorAPI.changePassword(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.changePassword(anyString, anyString);
                times = 1;
            }
        };

    }

    @Test
    public void testGatherLog() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.gatherLog(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.gatherLog(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testDetect() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.detect(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.detect(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testDetectForArr() throws BusinessException {
        String[] terminalIdArr = {"1", "2", "3"};

        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            public DefaultResponse detect(CbbTerminalIdRequest request) throws BusinessException {
                return DefaultResponse.Builder.success();
            }
        };

        try {
            CbbTerminalBatDetectRequest request = new CbbTerminalBatDetectRequest();
            request.setTerminalIdArr(terminalIdArr);
            terminalOperatorAPI.detect(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                terminalOperatorAPI.detect((CbbTerminalIdRequest) any);
                times = 3;
            }
        };

    }

    @Test
    public void testGetTerminalLogNameIsNull() {
        new Expectations() {
            {
                gatherLogCacheManager.getCache(anyString);
                result = null;
            }
        };
        String terminalId = "123";
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            terminalOperatorAPI.getTerminalLogName(request);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
    }

    @Test
    public void testGetTerminalLogNameStateIsFailure() {
        GatherLogCache cache = new GatherLogCache();
        cache.setState(GatherLogStateEnums.FAILURE);
        new Expectations() {
            {
                gatherLogCacheManager.getCache(anyString);
                result = cache;
            }
        };
        String terminalId = "123";
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            terminalOperatorAPI.getTerminalLogName(request);
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
        new Expectations() {
            {
                gatherLogCacheManager.getCache(anyString);
                result = cache;
            }
        };
        String terminalId = "123";
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            CbbTerminalNameResponse result = terminalOperatorAPI.getTerminalLogName(request);
            Assert.assertEquals(result.getTerminalName(), logName);

        } catch (BusinessException e) {
            fail();
        }
    }
}
