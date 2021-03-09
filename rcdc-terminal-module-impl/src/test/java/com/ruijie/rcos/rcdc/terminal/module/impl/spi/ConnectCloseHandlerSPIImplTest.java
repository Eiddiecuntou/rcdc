package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.log.Logger;
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

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

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
    public void testDispatch(@Capturing Logger logger) {
        final CbbDispatcherRequest request = new CbbDispatcherRequest();
        final String terminalId = "123";
        request.setTerminalId(terminalId);

        // 终端记录存在的场景
        final TerminalEntity terminalEntity = new TerminalEntity();
        new Expectations() {
            {
                terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = terminalEntity;
            }
        };
        spiImpl.dispatch(request);

        new Verifications() {
            {
                logger.warn("不存在terminalId=[{}]的终端记录，无需进行终端离线状态更新处理", terminalId);
                times = 0;

                basicInfoService.modifyTerminalStateToOffline(terminalId);
                times = 1;

                CbbNoticeRequest cbbNoticeRequest;
                terminalEventNoticeSPI.notify(cbbNoticeRequest = withCapture());
                assertEquals(terminalId, cbbNoticeRequest.getTerminalBasicInfo().getTerminalId());
                assertEquals(CbbNoticeEventEnums.OFFLINE.getName(), cbbNoticeRequest.getDispatcherKey());
                times = 1;
            }
        };

        // 终端记录不存在的场景
        new Expectations() {
            {
                terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = null;
            }
        };
        spiImpl.dispatch(request);

        new Verifications() {
            {
                logger.warn("不存在terminalId=[{}]的终端记录，无需进行终端离线状态更新处理", terminalId);
                times = 1;

                basicInfoService.modifyTerminalStateToOffline(terminalId);
                times = 0;

                CbbNoticeRequest cbbNoticeRequest;
                terminalEventNoticeSPI.notify(cbbNoticeRequest = withCapture());
                assertEquals(terminalId, cbbNoticeRequest.getTerminalBasicInfo().getTerminalId());
                assertEquals(CbbNoticeEventEnums.OFFLINE.getName(), cbbNoticeRequest.getDispatcherKey());
                times = 1;
            }
        };
    }

}
