package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalLogNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.offlinelogin.OfflineLoginSettingRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.offlinelogin.OfflineLoginSettingResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;

import mockit.*;
import mockit.integration.junit4.JMockit;

import java.io.File;

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
    private CollectLogCacheManager collectLogCacheManager;

    @Injectable
    private TerminalDetectService detectService;

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Injectable
    private CbbTerminalBasicInfoAPI basicInfoAPI;

    /**
     * 测试关机
     * 
     * @throws BusinessException 业务异常
     */
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

    /**
     * 测试重启
     * 
     * @throws BusinessException 业务异常
     */
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

    /**
     * 测试收集日志
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCollectLog() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.collectLog(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.collectLog(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试changePassword，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testChangePasswordArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.changePassword(null), "CbbChangePasswordRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试changePassword，
     * 
     * @throws Exception 异常
     */
    @Test
    public void testChangePassword() throws Exception {
        CbbChangePasswordRequest request = new CbbChangePasswordRequest();
        request.setPassword("password123");
        DefaultResponse response = terminalOperatorAPI.changePassword(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                operatorService.changePassword(request.getPassword());
                times = 1;
            }
        };
    }

    /**
     * 测试detect，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDetectArgumentIsNull() throws Exception {
        CbbTerminalIdRequest request = null;
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.singleDetect(request), "CbbTerminalIdRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试detect，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testDetect() throws BusinessException {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest("123");
        DefaultResponse response = terminalOperatorAPI.singleDetect(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                operatorService.detect("123");
                times = 1;
            }
        };
    }

    /**
     * 测试getCollectLog，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLogArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getCollectLog(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试getCollectLog，
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLog() throws Exception {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("logFileName");
        cache.setState(CbbCollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        CbbTerminalCollectLogStatusResponse response = terminalOperatorAPI.getCollectLog(request);
        assertEquals("logFileName", response.getLogName());
        assertEquals(CbbCollectLogStateEnums.DONE, response.getState());
    }

    /**
     * 测试getCollectLog，CollectLogCache为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetCollectLogCollectLogCacheIsNull() throws BusinessException {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = null;
            }
        };

        CbbTerminalCollectLogStatusResponse collectLog = terminalOperatorAPI.getCollectLog(request);
        assertEquals(CbbCollectLogStateEnums.FAULT, collectLog.getState());

    }

    /**
     * 测试getTerminalLogFileInfo，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetTerminalLogFileInfoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getTerminalLogFileInfo(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试getTerminalLogFileInfo，文件不存在
     */
    @Test
    public void testGetTerminalLogFileInfoCollectFileNotExist() {
        CbbTerminalLogNameRequest request = new CbbTerminalLogNameRequest();
        request.setLogName("123.rar");
        try {
            terminalOperatorAPI.getTerminalLogFileInfo(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试getTerminalLogFileInfo，收集失败
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetTerminalLogFileInfoHasSuffix() throws BusinessException {
        CbbTerminalLogNameRequest request = new CbbTerminalLogNameRequest();
        request.setLogName("logFileName.rar");
        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            private void checkFileExist(String logFilePath) throws BusinessException {
                
            }
        };

        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(request);
        assertEquals("/opt/ftp/terminal/log/logFileName.rar", response.getLogFilePath());
        assertEquals("logFileName", response.getLogFileName());
        assertEquals("rar", response.getSuffix());
    }

    /**
     * 测试getTerminalLogFileInfo，日志文件没有后缀名
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testGetTerminalLogFileInfoNotHasSuffix() throws BusinessException {
        CbbTerminalLogNameRequest request = new CbbTerminalLogNameRequest();
        request.setLogName("logFileName");
        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            private void checkFileExist(String logFilePath) throws BusinessException {
                
            }
        };

        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(request);
        assertEquals("/opt/ftp/terminal/log/logFileName", response.getLogFilePath());
        assertEquals("logFileName", response.getLogFileName());
        assertEquals("", response.getSuffix());
    }

    /**
     * 测试getTerminalLogFileInfo，日志文件存在
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testGetTerminalLogFileExist() throws BusinessException {
        CbbTerminalLogNameRequest request = new CbbTerminalLogNameRequest();
        request.setLogName("logFileName");
        new MockUp<File>() {
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(request);
        assertEquals("/opt/ftp/terminal/log/logFileName", response.getLogFilePath());
        assertEquals("logFileName", response.getLogFileName());
        assertEquals("", response.getSuffix());
    }
    
    
    /**
     *测试清空数据盘
     *
     *@throws BusinessException 业务异常
     */
    @Test
    public void testClearIdvTerminalDataDisk() throws BusinessException {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        DefaultResponse response = terminalOperatorAPI.clearIdvTerminalDataDisk(request);
        assertEquals(response.getStatus(),Status.SUCCESS);
    }


    /**
     *测试IDV终端离线登录设置
     *
     *@throws BusinessException 业务异常
     */
    @Test
    public void testIdvOfflineLoginSetting() throws BusinessException {
        OfflineLoginSettingRequest request = new OfflineLoginSettingRequest(0);
        DefaultResponse response = terminalOperatorAPI.idvOfflineLoginSetting(request);
        assertEquals(response.getStatus(), Status.SUCCESS);
    }

    /**
     * 测试 relieveFault 方法入参
     * @throws Exception
     */
    @Test
    public void testRelieveFaultValidateParams() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.relieveFault(null),
                "CbbTerminalIdRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试 relieveFault 方法
     */
    @Test
    public void testRelieveFault() {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.relieveFault(request);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                try {
                    operatorService.relieveFault(anyString);
                    times = 1;
                } catch (BusinessException e) {
                    fail();
                }
            }
        };
    }


    /**
     *测试queryOfflineLoginSetting
     *
     *@throws BusinessException 业务异常
     */
    @Test
    public void testQueryOfflineLoginSetting() throws BusinessException {

        new Expectations() {
            {
                operatorService.queryOfflineLoginSetting();
                result = "0";
            }
        };
        final OfflineLoginSettingResponse offlineLoginSettingResponse = terminalOperatorAPI.queryOfflineLoginSetting(new DefaultRequest());
        Assert.assertEquals("0", offlineLoginSettingResponse.getOfflineAutoLocked());
    }

}
