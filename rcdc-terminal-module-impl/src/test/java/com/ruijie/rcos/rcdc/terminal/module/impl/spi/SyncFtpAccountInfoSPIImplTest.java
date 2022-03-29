package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalFtpConfigInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: SyncFtpAccountInfoSPIImpl测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/10 3:03 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class SyncFtpAccountInfoSPIImplTest {

    @Tested
    SyncFtpAccountInfoSPIImpl syncFtpAccountInfoSPI;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 测试dispatch方法
     */
    @Test
    public void testDispatch() {
        new Expectations(AesUtil.class) {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result = "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
                AesUtil.encrypt("21Wq_Er", Constants.FTP_PASSWORD_KEY);
                result = "aaa21Wq_Er";
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey("sync_ftp_account_info");
        syncFtpAccountInfoSPI.dispatch(request);
        new Verifications() {
            {
                CbbResponseShineMessage message;
                messageHandlerAPI.response(message = withCapture());
                times = 1;
                Assert.assertTrue(0 == message.getCode());
                Assert.assertTrue(2021 == ((TerminalFtpConfigInfo)message.getContent()).getFtpPort());
                Assert.assertEquals("shine", ((TerminalFtpConfigInfo)message.getContent()).getFtpUserName());
                Assert.assertEquals("/", ((TerminalFtpConfigInfo)message.getContent()).getFtpPath());
                Assert.assertEquals("/", ((TerminalFtpConfigInfo)message.getContent()).getFileDir());
            }
        };

    }

    /**
     * 测试dispatch方法，数据库中不存在终端ftp配置信息
     */
    @Test
    public void testDispatchTerminalFtpConfigUnexists() {
        new Expectations(AesUtil.class) {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result = null;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey("sync_ftp_account_info");
        syncFtpAccountInfoSPI.dispatch(request);
        new Verifications() {
            {
                CbbResponseShineMessage message;
                messageHandlerAPI.response(message = withCapture());
                times = 1;
                Assert.assertTrue(99 == message.getCode());
            }
        };
    }
}
