package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.compatible.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalPassword;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class SyncTerminalPasswordHandlerSPIImplTest {

    @Tested
    private SyncTerminalPasswordHandlerSPIImpl spiImpl;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private TerminalOperatorService terminalOperatorService;

    /**
     * 测试dispatch,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> spiImpl.dispatch(null), "CbbDispatcherRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试dispatch,
     * 
     * @param utils mock MessageUtils
     * @throws BusinessException 异常
     */
    @Test
    public void testDispatch(@Mocked MessageUtils utils) throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage<>();
        new Expectations() {
            {
                terminalOperatorService.getTerminalPassword();
                result = "123456";
                MessageUtils.buildResponseMessage(request, any);
                result = responseMessage;
            }
        };
        spiImpl.dispatch(request);
        new Verifications() {
            {
                TerminalPassword terminalPassword;
                MessageUtils.buildResponseMessage(request, terminalPassword = withCapture());
                times = 1;
                assertEquals("123456", terminalPassword.getPassword());
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }

    /**
     * 测试dispatch失败
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testDispatchFail() throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        new Expectations() {
            {
                terminalOperatorService.getTerminalPassword();
                result = new BusinessException("key");
            }
        };
        spiImpl.dispatch(request);
        new Verifications() {
            {
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 0;
            }
        };
    }
}
