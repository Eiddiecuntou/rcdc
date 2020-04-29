package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import java.io.IOException;
import java.util.UUID;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
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

    @Mocked
    ProcessBuilder processBuilder;

    @Mocked
    Process process;

    /**
     * 测试sateInit方法
     */
    @Test
    public void testSafeInit() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        new Expectations(UUID.class) {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result =  "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
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
     * @throws IOException ex
     */
    @Test
    public void testSafeInitExecuteSystemCmdFail() throws IOException {
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result =  "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
                processBuilder.start();
                result = new IOException();
            }
        };
        terminalFtpAccountInfoInit.safeInit();
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
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitExecuteSystemCmdCodeNotZero() throws InterruptedException {
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result =  "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
                process.waitFor();
                result = 1;
            }
        };
        terminalFtpAccountInfoInit.safeInit();
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
