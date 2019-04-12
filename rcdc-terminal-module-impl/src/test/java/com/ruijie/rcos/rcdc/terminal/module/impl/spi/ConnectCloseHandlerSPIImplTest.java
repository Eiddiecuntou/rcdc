package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class ConnectCloseHandlerSPIImplTest {

    @Tested
    private ConnectCloseHandlerSPIImpl spiImpl;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    /**
     * 测试dispatch,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> spiImpl.dispatch(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试dispatch,
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("123");
        spiImpl.dispatch(request);

        new Verifications() {
            {
                basicInfoService.modifyTerminalStateToOffline("123");
                times = 1;
                CbbNoticeRequest cbbNoticeRequest;
                terminalEventNoticeSPI.notify(cbbNoticeRequest = withCapture());
                times = 1;
                assertEquals("123", cbbNoticeRequest.getTerminalId());
                assertEquals(NoticeEventEnums.OFFLINE.getName(), cbbNoticeRequest.getDispatcherKey());
            }
        };
    }

}
