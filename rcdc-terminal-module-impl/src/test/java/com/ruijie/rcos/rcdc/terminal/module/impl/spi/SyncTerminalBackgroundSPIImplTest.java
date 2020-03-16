package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import mockit.*;

/**
 * Description: 同步终端背景图SPI测试类
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/8
 *
 * @author songxiang
 */

@RunWith(SkyEngineRunner.class)
public class SyncTerminalBackgroundSPIImplTest {

    @Tested
    SyncTerminalBackgroundSPIImpl syncTerminalBackgroundSPI;

    @Injectable
    GlobalParameterAPI globalParameterAPI;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private Logger logger;

    @Mocked
    MessageUtils messageUtils;


    private static final String REQUEST_DATA = "{'isDefaultImage':false,detailInfo:{'md5':'123','imageName':'123','imagePath':'abc/background.png'}}";
    private static final String GLOBAL_DATA = "{'isDefaultImage':false,detailInfo:{'md5':'123','imageName':'123','imagePath':'abc/background.png'}}";

    /**
     * 测试收到终端的同步请求的时候，全局参数表为空的情况
     */
    @Test
    public void testDispatchWhenGlobalParamIsNull() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> syncTerminalBackgroundSPI.dispatch(null),
                    "cbbDispatcherRequest must not be null");
        } catch (Exception e) {
            Assert.fail();
        }
        CbbDispatcherRequest cbbDispatcherRequest = new CbbDispatcherRequest();
        cbbDispatcherRequest.setData(REQUEST_DATA);
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = null;
            }
        };
        syncTerminalBackgroundSPI.dispatch(cbbDispatcherRequest);
        new Verifications() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                times = 1;
            }
        };
    }

    /**
     * 测试收到终端的同步请求的时候，不需要同步
     */
    @Test
    public void testDispatchWhenNoNeedSync() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> syncTerminalBackgroundSPI.dispatch(null),
                    "cbbDispatcherRequest must not be null");
        } catch (Exception e) {
            Assert.fail();
        }
        CbbDispatcherRequest cbbDispatcherRequest = new CbbDispatcherRequest();
        cbbDispatcherRequest.setData(REQUEST_DATA);
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = GLOBAL_DATA;
            }
        };
        syncTerminalBackgroundSPI.dispatch(cbbDispatcherRequest);
        new Verifications() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                times = 1;
            }
        };
    }

    /**
     * 测试收到终端的同步请求的时候，需要同步
     */
    @Test
    public void testDispatchWhenNeedSync() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> syncTerminalBackgroundSPI.dispatch(null),
                    "cbbDispatcherRequest must not be null");
        } catch (Exception e) {
            Assert.fail();
        }
        CbbDispatcherRequest cbbDispatcherRequest = new CbbDispatcherRequest();
        cbbDispatcherRequest.setData(REQUEST_DATA);
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
            }
        };
        syncTerminalBackgroundSPI.dispatch(cbbDispatcherRequest);
        new Verifications() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                times = 1;
            }
        };
    }
}
