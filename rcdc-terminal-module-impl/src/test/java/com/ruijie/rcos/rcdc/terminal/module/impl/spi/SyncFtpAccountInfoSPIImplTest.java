package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.codec.compatible.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.FtpConfigInfo;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

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
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminal_ftp_config");
                result = "{\"ftpPort\": 2021,\"ftpUserName\": \"shine\",\"ftpUserPassword\": \"21Wq_Er\","
                    + "\"ftpPath\": \"/\",\"fileDir\": \"/\"}";
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
                Assert.assertTrue(2021 == ((FtpConfigInfo)message.getContent()).getFtpPort());
                Assert.assertEquals("shine", ((FtpConfigInfo)message.getContent()).getFtpUserName());
                Assert.assertEquals("21Wq_Er",
                    AesUtil.descrypt(((FtpConfigInfo)message.getContent()).getFtpUserPassword(), "SHINEFTPPASSWORD"));
                Assert.assertEquals("/", ((FtpConfigInfo)message.getContent()).getFtpPath());
                Assert.assertEquals("/", ((FtpConfigInfo)message.getContent()).getFileDir());
            }
        };

    }

    /**
     * 测试dispatch方法，数据库中不存在终端ftp配置信息
     */
    @Test
    public void testDispatchTerminalFtpConfigUnexists() {
        new Expectations() {
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
