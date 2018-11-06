package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.TranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.ShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.TerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.DispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.NoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalBasicInfoEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CheckUpgradeHandlerImplTest {

    @Tested
    private CheckUpgradeHandlerImpl checkUpgradeHandler;

    @Injectable
    private TranspondMessageHandlerAPI messageHandlerAPI;
    @Injectable
    private TerminalEventNoticeSPI terminalEventNoticeSPI;
    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Test
    public void testDispatchUpdateTerminalBasicInfo() {
        String terminalId = "123";
        TerminalBasicInfoEntity entity = new TerminalBasicInfoEntity();
        entity.setTerminalId("123456");
        entity.setName("t-box3");
        entity.setCpuMode("intel");
        new Expectations() {{
            terminalEventNoticeSPI.notify((NoticeRequest) any);
            basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
            result = entity;
            try {
                messageHandlerAPI.response((ShineMessageRequest) any);
            } catch (BusinessException e) {
                fail();
            }
        }};

        try {
            DispatcherRequest request = new DispatcherRequest();
            request.setTerminalId(terminalId);
            request.setRequestId("456");
            request.setData(generateJson());
            checkUpgradeHandler.dispatch(request);

            saveVerifications();
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void testDispatchAddTerminalBasicInfo() {
        String terminalId = "123";
        new Expectations() {{
            terminalEventNoticeSPI.notify((NoticeRequest) any);
            basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
            result = null;
            try {
                messageHandlerAPI.response((ShineMessageRequest) any);
            } catch (BusinessException e) {
                fail();
            }
        }};

        try {
            DispatcherRequest request = new DispatcherRequest();
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateJson());
            checkUpgradeHandler.dispatch(request);

            saveVerifications();
        } catch (Exception e) {
            fail();
        }
    }

    private void saveVerifications() {
        new Verifications() {{
            TerminalBasicInfoEntity basicInfoEntity;
            basicInfoDAO.save(basicInfoEntity = withCapture());
            assertEquals(basicInfoEntity.getTerminalId(), "123");
            assertEquals(basicInfoEntity.getName(), "t-box2");
            assertEquals(basicInfoEntity.getCpuMode(), "intel5");
        }};
    }


    private String generateJson() {
        ShineTerminalBasicInfo info = new ShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setName("t-box2");
        info.setCpuMode("intel5");
        return JSON.toJSONString(info);
    }

}