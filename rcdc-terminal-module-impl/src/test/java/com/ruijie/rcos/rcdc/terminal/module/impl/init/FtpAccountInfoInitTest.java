package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.ruijie.rcos.rcdc.terminal.module.impl.util.CmdExecuteUtil;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: FtpAccountInfoInit测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/10 1:55 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class FtpAccountInfoInitTest {

    @Tested
    FtpAccountInfoInit ftpAccountInfoInit;

    @Injectable
    GlobalParameterAPI globalParameterAPI;

    @Mocked
    CmdExecuteUtil cmdExecuteUtil;

    /**
     * 测试sateInit方法
     */
    @Test
    public void testSafeInit() {
        new Expectations() {
            {
                globalParameterAPI.findParameter("guesttool_log_ftp_config");
                result = "{\"ftpPort\": 2021,\"ftpUserName\": \"vmtoolFtp\",\"ftpUserPassword\": \"21Wq_Er>L\",\"ftpPath\": \"/\",\"fileDir\": \"/data/guesttool\"}";
                globalParameterAPI.findParameter("terminal_ftp_config");
                result =  "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
            }
        };
        ftpAccountInfoInit.safeInit();
        new Verifications() {
            {
                globalParameterAPI.updateParameter(withEqual("terminal_ftp_config"), anyString);
                times = 1;
                globalParameterAPI.updateParameter(withEqual("guesttool_log_ftp_config"), anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试sateInit方法, key为"guesttool_log_ftp_config"的数据不存在
     */
    @Test
    public void testSafeInitGuesttoolLogFtpConfigUnexists() {
        new Expectations() {
            {
                globalParameterAPI.findParameter("guesttool_log_ftp_config");
                result = null;
                globalParameterAPI.findParameter("terminal_ftp_config");
                result =  "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
            }
        };
        ftpAccountInfoInit.safeInit();
        new Verifications() {
            {
                globalParameterAPI.updateParameter(withEqual("terminal_ftp_config"), anyString);
                times = 1;
                globalParameterAPI.updateParameter(withEqual("guesttool_log_ftp_config"), anyString);
                times = 0;
            }
        };
    }
}
