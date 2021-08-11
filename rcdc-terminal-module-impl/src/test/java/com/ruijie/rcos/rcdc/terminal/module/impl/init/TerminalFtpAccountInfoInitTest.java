package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connector.tcp.api.TerminalFtpAccountInfoAPI;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.apache.commons.compress.utils.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: TerminalFtpAccountInfoInit测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/10 1:55 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class TerminalFtpAccountInfoInitTest {

    @Tested
    TerminalFtpAccountInfoInit terminalFtpAccountInfoInit;

    @Injectable
    GlobalParameterAPI globalParameterAPI;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalFtpAccountInfoAPI terminalFtpAccountInfoAPI;

    @Mocked
    ProcessBuilder processBuilder;

    @Mocked
    Process process;

    /**
     * 初始化加载
     */
    @Before
    public void before() {
        new Expectations(System.class) {
            {
                System.getProperty(anyString);
                result = "Linux";
            }
        };
    }

    /**
     * 测试sateInit方法
     */
    @Test
    public void testSafeInit() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        new Expectations(UUID.class) {
            {

                globalParameterAPI.findParameter("terminal_ftp_config");
                result = "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\"," + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
                UUID.randomUUID();
                result = uuid;
            }
        };
        terminalFtpAccountInfoInit.safeInit();
        new Verifications() {
            {
                String info;
                globalParameterAPI.updateParameter(withEqual("terminal_ftp_config"), info = withCapture());
                times = 1;
                Assert.assertTrue(info.contains("00000000"));
            }
        };
    }

    /**
     * 测试sateInit方法, 执行系统命令失败
     *
     * @throws Exception ex
     */
    @Test
    public void testSafeInitExecuteSystemCmdFail() throws Exception {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        List<String> onlineTerminalIdList = Lists.newArrayList();
        onlineTerminalIdList.add("1.1.1.1");
        new Expectations(UUID.class) {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result = "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\"," + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
                UUID.randomUUID();
                result = uuid;
                processBuilder.start();
                result = new IOException();
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
            }
        };
        terminalFtpAccountInfoInit.safeInit();
        Thread.sleep(1000);
        new Verifications() {
            {
                String info;
                globalParameterAPI.updateParameter(withEqual("terminal_ftp_config"), info = withCapture());
                times = 1;
                Assert.assertTrue(info.contains("21Wq_Er"));
            }
        };
    }

    /**
     * 测试sateInit方法, 执行系统命令失败
     * 
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitExecuteSystemCmdCodeNotZero() throws InterruptedException {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        List<String> onlineTerminalIdList = Lists.newArrayList();
        onlineTerminalIdList.add("1.1.1.1");
        new Expectations(UUID.class) {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result = "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\"," + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
                UUID.randomUUID();
                result = uuid;
                process.waitFor();
                result = 1;
                sessionManager.getOnlineTerminalId();
                result = onlineTerminalIdList;
            }
        };
        terminalFtpAccountInfoInit.safeInit();
        Thread.sleep(1000);
        new Verifications() {
            {
                String info;
                globalParameterAPI.updateParameter(withEqual("terminal_ftp_config"), info = withCapture());
                times = 1;
                Assert.assertTrue(info.contains("21Wq_Er"));
            }
        };
    }
}
