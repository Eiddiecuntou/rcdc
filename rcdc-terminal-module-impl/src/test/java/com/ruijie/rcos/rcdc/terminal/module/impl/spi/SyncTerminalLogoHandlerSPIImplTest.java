package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalLogoName;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/4
 *
 * @author hs
 */
@RunWith(JMockit.class)
public class SyncTerminalLogoHandlerSPIImplTest {

    @Tested
    private SyncTerminalLogoHandlerSPIImpl syncTerminalLogoHandlerSPI;

    @Injectable
    private TerminalLogoService terminalLogoService;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 测试dispatch,参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> syncTerminalLogoHandlerSPI.dispatch(null), "CbbDispatcherRequest不能为空");
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
                terminalLogoService.getTerminalLogoName();
                result = "logo.png";
                MessageUtils.buildResponseMessage(request, any);
                result = responseMessage;
            }
        };
        syncTerminalLogoHandlerSPI.dispatch(request);
        new Verifications() {
            {
                TerminalLogoName terminalLogo;
                MessageUtils.buildResponseMessage(request, terminalLogo = withCapture());
                times = 1;
                assertEquals("logo.png", terminalLogo.getLogoName());
                messageHandlerAPI.response(responseMessage);
                times = 1;
            }
        };
    }

    /**
     * 测试dispatch, 失败
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testDispatchFail() throws BusinessException {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        new Expectations() {
            {
                terminalLogoService.getTerminalLogoName();
                result = new BusinessException("key");
            }
        };
        syncTerminalLogoHandlerSPI.dispatch(request);
        new Verifications() {
            {
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 0;
            }
        };
    }
}
