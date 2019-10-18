package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(SkyEngineRunner.class)
public class CheckUpgradeHandlerSPIImplTest {

    @Tested
    private CheckUpgradeHandlerSPIImpl checkUpgradeHandler;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private CbbTerminalEventNoticeSPI cbbTerminalEventNoticeSPI;

    @Injectable
    private TerminalComponentUpgradeService componentUpgradeService;

    @Injectable
    private TerminalBasicInfoService basicInfoService;


    /**
     * 测试检查组件升级- 更新终端信息
     */
    @Test
    public void testDispatchUpdateTerminalBasicInfo() {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        entity.setTerminalOsType("Linux");
        entity.setPlatform(CbbTerminalPlatformEnums.VDI);
        new Expectations() {
            {
                basicInfoService.saveBasicInfo(anyString, (ShineTerminalBasicInfo) any);
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        new MockUp(CbbTerminalTypeEnums.class) {
            @Mock
            public CbbTerminalTypeEnums convert(String platform, String osType) {
                return CbbTerminalTypeEnums.VDI_LINUX;
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

    /**
     * 测试检查组件升级-添加终端信息
     */
    @Test
    public void testDispatchAddTerminalBasicInfo() {
        String terminalId = "123";
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = null;
                basicInfoService.saveBasicInfo(anyString, (ShineTerminalBasicInfo) any);
                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        new MockUp(CbbTerminalTypeEnums.class) {
            @Mock
            public CbbTerminalTypeEnums convert(String typeName) {
                return CbbTerminalTypeEnums.VDI_LINUX;
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
                basicInfoService.saveBasicInfo(anyString, (ShineTerminalBasicInfo) any);
                times = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;
            }
        };
    }


    private String generateJson() {
        ShineTerminalBasicInfo info = new ShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        return JSON.toJSONString(info);
    }

}
