package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
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
public class CheckUpgradeHandlerSPIImplTest {

    @Tested
    private CheckUpgradeHandlerSPIImpl checkUpgradeHandler;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;

    @Test
    public void testDispatchUpdateTerminalBasicInfo() {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        new Expectations() {{
            basicInfoDAO.findTerminalEntityByTerminalId(anyString);
            result = entity;
            try {
                messageHandlerAPI.response((CbbResponseShineMessage) any);
            } catch (Exception e) {
                fail();
            }
        }};

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
        new Expectations() {{
            basicInfoDAO.findTerminalEntityByTerminalId(anyString);
            result = null;
            try {
                messageHandlerAPI.response((CbbResponseShineMessage) any);
            } catch (Exception e) {
                fail();
            }
        }};

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
        new Verifications() {{
            TerminalEntity basicInfoEntity;
            basicInfoDAO.save(basicInfoEntity = withCapture());
            assertEquals(basicInfoEntity.getTerminalId(), "123");
            assertEquals(basicInfoEntity.getTerminalName(), "t-box2");
            assertEquals(basicInfoEntity.getCpuType(), "intel5");
        }};
    }


    private String generateJson() {
        ShineTerminalBasicInfo info = new ShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuMode("intel5");
        return JSON.toJSONString(info);
    }

}