package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.CbbTerminalEntity;
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
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;
    @Injectable
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;
    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Test
    public void testDispatchUpdateTerminalBasicInfo() {
        String terminalId = "123";
        CbbTerminalEntity entity = new CbbTerminalEntity();
        entity.setTerminalId("123456");
        entity.setName("t-box3");
        entity.setCpuMode("intel");
        new Expectations() {
            {
                cbbTerminalEventNoticeSPI.notify((CbbNoticeRequest) any);
                basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
                result = entity;
                try {
                    messageHandlerAPI.response((CbbShineMessageRequest) any);
                } catch (BusinessException e) {
                    fail();
                }
            }
        };

        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
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
        new Expectations() {
            {
                cbbTerminalEventNoticeSPI.notify((CbbNoticeRequest) any);
                basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(anyString);
                result = null;
                try {
                    messageHandlerAPI.response((CbbShineMessageRequest) any);
                } catch (BusinessException e) {
                    fail();
                }
            }
        };

        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
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
        new Verifications() {
            {
                CbbTerminalEntity basicInfoEntity;
                basicInfoDAO.save(basicInfoEntity = withCapture());
                assertEquals(basicInfoEntity.getTerminalId(), "123");
                assertEquals(basicInfoEntity.getName(), "t-box2");
                assertEquals(basicInfoEntity.getCpuMode(), "intel5");
            }
        };
    }


    private String generateJson() {
        ShineTerminalBasicInfo info = new ShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setName("t-box2");
        info.setCpuMode("intel5");
        return JSON.toJSONString(info);
    }

}
