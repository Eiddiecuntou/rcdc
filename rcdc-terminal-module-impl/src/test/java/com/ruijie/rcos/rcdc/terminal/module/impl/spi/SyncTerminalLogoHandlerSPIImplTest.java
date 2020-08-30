package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.compatible.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalLogoInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
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
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        new Expectations() {
            {
                terminalLogoService.getTerminalLogoInfo();
                result = terminalLogoInfo;
                MessageUtils.buildResponseMessage(request, any);
                result = responseMessage;
            }
        };
        syncTerminalLogoHandlerSPI.dispatch(request);
        new Verifications() {
            {
                TerminalLogoInfo terminalLogo;
                MessageUtils.buildResponseMessage(request, terminalLogo = withCapture());
                times = 1;
                assertEquals("/logo/logo.png", terminalLogo.getLogoPath());
                assertEquals("123456", terminalLogo.getMd5());
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
                terminalLogoService.getTerminalLogoInfo();
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
