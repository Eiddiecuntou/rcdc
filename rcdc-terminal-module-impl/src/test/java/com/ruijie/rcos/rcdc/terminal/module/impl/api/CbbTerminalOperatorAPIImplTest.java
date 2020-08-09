package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.offlinelogin.OfflineLoginSettingRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

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

            terminalOperatorAPI.shutdown(terminalId);
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

            terminalOperatorAPI.shutdown(terminalId);
            terminalOperatorAPI.restart(terminalId);
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

            terminalOperatorAPI.collectLog(terminalId);
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
        terminalOperatorAPI.changePassword(request);
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

        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.singleDetect(null), "terminalId不能为空");
        assertTrue(true);
    }

    /**
     * 测试detect，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testDetect() throws BusinessException {
        terminalOperatorAPI.singleDetect("123");
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
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getCollectLog(null), "terminalId can not be blank");
        assertTrue(true);
    }

    /**
     * 测试getCollectLog，
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLog() throws Exception {
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("logFileName");
        cache.setState(CbbCollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        CbbTerminalCollectLogStatusResponse response = terminalOperatorAPI.getCollectLog("123");
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
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = null;
            }
        };

        CbbTerminalCollectLogStatusResponse collectLog = terminalOperatorAPI.getCollectLog("123");
        assertEquals(CbbCollectLogStateEnums.FAULT, collectLog.getState());

    }

    /**
     * 测试getTerminalLogFileInfo，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetTerminalLogFileInfoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getTerminalLogFileInfo(null), "logFileName can not be blank");
        assertTrue(true);
    }

    /**
     * 测试getTerminalLogFileInfo，文件不存在
     */
    @Test
    public void testGetTerminalLogFileInfoCollectFileNotExist() {
        String logName = "123.rar";
        try {
            terminalOperatorAPI.getTerminalLogFileInfo(logName);
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
        String logName = "logFileName.rar";
        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            private void checkFileExist(String logFilePath) throws BusinessException {
                
            }
        };

        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(logName);
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
        String logName = "logFileName";
        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            private void checkFileExist(String logFilePath) throws BusinessException {
                
            }
        };

        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(logName);
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
        String logName = "logFileName";
        new MockUp<File>() {
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(logName);
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
        terminalOperatorAPI.clearIdvTerminalDataDisk("123");
        new Verifications() {
            {
                operatorService.diskClear("123");
                times = 1;

            }
        };
    }


    /**
     *测试IDV终端离线登录设置
     *
     *@throws BusinessException 业务异常
     */
    @Test
    public void testIdvOfflineLoginSetting() throws BusinessException {
        OfflineLoginSettingRequest request = new OfflineLoginSettingRequest(0);
        terminalOperatorAPI.idvOfflineLoginSetting(request);
        new Verifications() {
            {
                operatorService.offlineLoginSetting(0);
                times = 1;
            }
        };
    }

    /**
     * 测试 relieveFault 方法入参
     * @throws Exception
     */
    @Test
    public void testRelieveFaultValidateParams() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.relieveFault(null),
                "terminalId不能为空");
        assertTrue(true);
    }

    /**
     * 测试 relieveFault 方法
     */
    @Test
    public void testRelieveFault() {
        try {
            String terminalId = "123";

            terminalOperatorAPI.relieveFault(terminalId);
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

        Assert.assertEquals("0", terminalOperatorAPI.queryOfflineLoginSetting());
    }

}
