package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalCollectLogStatusDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLogFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.TerminalCollectLogCleanQuartzTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.text.ParseException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/20
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalLogAPIimplTest {

    @Tested
    private CbbTerminalLogAPIImpl cbbTerminalLogAPI;

    @Injectable
    private TerminalOperatorService operatorService;

    @Injectable
    private CollectLogCacheManager collectLogCacheManager;

    @Injectable
    private TerminalDetectService detectService;

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Injectable
    private TerminalCollectLogCleanQuartzTask terminalCollectLogCleanQuartzTask;

    /**
     * 测试收集日志
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCollectLog() throws BusinessException {
        try {
            String terminalId = "123";

            cbbTerminalLogAPI.collectLog(terminalId);
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
     * 测试getCollectLog，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLogArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> cbbTerminalLogAPI.getCollectLog(null), "terminalId can not be blank");
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
        CbbTerminalCollectLogStatusDTO response = cbbTerminalLogAPI.getCollectLog("123");
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

        CbbTerminalCollectLogStatusDTO collectLog = cbbTerminalLogAPI.getCollectLog("123");
        assertEquals(CbbCollectLogStateEnums.FAULT, collectLog.getState());

    }

    /**
     * 测试getTerminalLogFileInfo，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetTerminalLogFileInfoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> cbbTerminalLogAPI.getTerminalLogFileInfo(null), "logFileName can not be blank");
        assertTrue(true);
    }

    /**
     * 测试getTerminalLogFileInfo，文件不存在
     */
    @Test
    public void testGetTerminalLogFileInfoCollectFileNotExist() {
        String logName = "123.rar";
        try {
            cbbTerminalLogAPI.getTerminalLogFileInfo(logName);
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
        new MockUp<CbbTerminalLogAPIImpl>() {
            @Mock
            private void checkFileExist(String logFilePath) throws BusinessException {

            }
        };

        CbbTerminalLogFileInfoDTO response = cbbTerminalLogAPI.getTerminalLogFileInfo(logName);
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
        new MockUp<CbbTerminalLogAPIImpl>() {
            @Mock
            private void checkFileExist(String logFilePath) throws BusinessException {

            }
        };

        CbbTerminalLogFileInfoDTO response = cbbTerminalLogAPI.getTerminalLogFileInfo(logName);
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

        CbbTerminalLogFileInfoDTO response = cbbTerminalLogAPI.getTerminalLogFileInfo(logName);
        assertEquals("/opt/ftp/terminal/log/logFileName", response.getLogFilePath());
        assertEquals("logFileName", response.getLogFileName());
        assertEquals("", response.getSuffix());
    }

    @Test
    public void testStartDefaultCleanCollectLogTask() throws BusinessException, ParseException {
        String cronExpression = "0 0 2 * * ? *";

        cbbTerminalLogAPI.startDefaultCleanCollectLogTask();

        new Verifications() {
            {
                ThreadExecutors.scheduleWithCron(TerminalCollectLogCleanQuartzTask.class.getSimpleName(),
                        terminalCollectLogCleanQuartzTask, cronExpression);
                times = 1;
            }
        };
    }

    @Test
    public void testStartDefaultCleanCollectLogTaskWithException() throws BusinessException, ParseException {
        String cronExpression = "0 0 2 * * ? *";
        new Expectations(ThreadExecutors.class) {
            {
                ThreadExecutors.scheduleWithCron(TerminalCollectLogCleanQuartzTask.class.getSimpleName(),
                        terminalCollectLogCleanQuartzTask, cronExpression);;
                result = new RuntimeException("test");
            }
        };

        try {
            cbbTerminalLogAPI.startDefaultCleanCollectLogTask();
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_START_DEFAULT_CLEAN_COLLECT_LOG_FAIL);
        }
    }

}
