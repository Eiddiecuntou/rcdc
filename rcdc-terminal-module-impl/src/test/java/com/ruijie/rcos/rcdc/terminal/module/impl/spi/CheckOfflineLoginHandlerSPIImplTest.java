package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.junit.Test;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/18 19:18
 *
 * @author conghaifeng
 */
public class CheckOfflineLoginHandlerSPIImplTest {

    @Tested
    private CheckOfflineLoginHandlerSPIImpl checkOfflineLoginHandlerSPI;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;
    
    /**
     *测试dispatch
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        new Expectations() {
            {
                globalParameterAPI.findParameter(anyString);
                result = "15";
            }
        };
        checkOfflineLoginHandlerSPI.dispatch(request);
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }

    /**
     *测试dispatch，离线登录设置参数不存在
     */
    @Test
    public void testDispatchWithOutOfflineAutoLocked() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        checkOfflineLoginHandlerSPI.dispatch(request);
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }

    /**
     *测试dispatch，终端请求离线登录设置消息应答失败
     */
    @Test
    public void testDispatchWithException() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        new Expectations() {
            {
                globalParameterAPI.findParameter(anyString);
                result = "15";
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                result = new Exception();
            }
        };
        checkOfflineLoginHandlerSPI.dispatch(request);
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }
    


}