package com.ruijie.rcos.rcdc.terminal.module.impl.quartz;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;

import mockit.*;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年5月8日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class TerminalCollectLogCleanQuartzTaskTest {

    @Tested
    private TerminalCollectLogCleanQuartzTask quartz;

    @Injectable
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;


    /**
     * 测试execute，终端日志存放目录不存在
     * 
     * @throws Exception 异常
     */
    @Test
    public void testExecuteLogDirectoryNotExist() throws Exception {

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }
        };
        quartz.run();
        new Verifications() {
            {
                BaseCreateSystemLogRequest request;
                baseSystemLogMgmtAPI.createSystemLog(request = withCapture());
                times = 1;
                assertEquals(request.getContent(), BusinessKey.RCDC_TERMINAL_QUARTZ_CLEAN_TERMINAL_COLLECT_LOG_FAIL_SYSTEM_LOG);
            }
        };
    }

    /**
     * 测试execute, 终端日志文件夹无日志文件
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecuteLogDirectoryIsEmpty() throws Exception {
        new MockUp<File>() {
            @Mock
            public File[] listFiles() {
                return new File[0];
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }
        };

        quartz.run();
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 测试execute
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute() throws Exception {
        new MockUp<SkyengineFile>() {

            @Mock
            public boolean delete(boolean isMove) {
                return true;
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }
        };

        new MockUp<File>() {
            @Mock
            public File[] listFiles() {
                return new File[] {new File("111"), new File("222")};
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public long lastModified() {
                return new Date().getTime() - 1;
            }
        };

        quartz.run();
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 测试execute，删除文件失败
     *
     * @param skyengineFile 框架文件封装对象
     * @throws Exception 异常
     */
    @Test
    public void testExecuteDeleteFileFail(@Mocked SkyengineFile skyengineFile) throws Exception {

        new MockUp<SkyengineFile>() {

            @Mock
            public boolean delete(boolean isMove) {
                return false;
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }
        };

        new MockUp<File>() {
            @Mock
            public File[] listFiles() {
                return new File[] {new File("111"), new File("222")};
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public long lastModified() {
                return new Date().getTime() - TerminalCollectLogCleanQuartzTask.TERMINAL_LOG_FILE_EXPIRE_TIME;
            }
        };
        quartz.run();
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 测试execute,删除文件成功
     *
     * @param skyengineFile 框架文件封装对象
     * @throws Exception 异常
     */
    @Test
    public void testExecuteDeleteFileSuccess(@Mocked SkyengineFile skyengineFile) throws Exception {

        new MockUp<SkyengineFile>() {

            @Mock
            public boolean delete(boolean isMove) {
                return true;
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }
        };

        new MockUp<File>() {
            @Mock
            public File[] listFiles() {
                return new File[] {new File("111"), new File("222")};
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public long lastModified() {
                return new Date().getTime() - TerminalCollectLogCleanQuartzTask.TERMINAL_LOG_FILE_EXPIRE_TIME;
            }
        };
        quartz.run();
        new Verifications() {
            {
                BaseCreateSystemLogRequest request;
                baseSystemLogMgmtAPI.createSystemLog(request = withCapture());
                times = 1;
                assertEquals(request.getContent(), BusinessKey.RCDC_TERMINAL_QUARTZ_CLEAN_TERMINAL_COLLECT_LOG_SUCCESS_SYSTEM_LOG);
            }
        };
    }

    /**
     * 测试异常方法
     *
     * @throws ParseException 解析异常
     */
    @Test
    public void testSafeInitWhileException() throws ParseException {
        new Expectations(ThreadExecutors.class) {
            {
                ThreadExecutors.scheduleWithCron(anyString, (Runnable) any, anyString);
                result = new ParseException("", 0);
            }
        };
        try {
            quartz.safeInit();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "定时任务[" + quartz.getClass() + "]cron表达式[0 0 2 * * ? *]解析异常");
        }

        new Verifications() {
            {
                ThreadExecutors.scheduleWithCron(anyString, (Runnable) any, anyString);
                times = 1;
            }
        };
    }

}
