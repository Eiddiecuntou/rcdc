package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.SystemUpgradeCheckResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
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
@RunWith(SkyEngineRunner.class)
public class CheckSystemUpgradeHandlerSPIImplTest {

    @Tested
    private CheckSystemUpgradeHandlerSPIImpl checkSystemUpgradeHandlerSPI;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;


    /**
     * 测试检查组件升级- 更新终端信息
     */
    @Test
    public void testDispatchUpdateTerminalBasicInfo(@Mocked TerminalSystemUpgradeHandler handler) throws BusinessException {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        entity.setTerminalOsType("Linux");
        entity.setPlatform(CbbTerminalPlatformEnums.VDI);
        entity.setCpuArch(CbbCpuArchType.X86_64);

        SystemUpgradeCheckResult checkResult = new SystemUpgradeCheckResult();
        checkResult.setSystemUpgradeCode(100);
        checkResult.setContent("aaa");

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;

                handlerFactory.getHandler(TerminalTypeArchType.VDI_LINUX_X86);
                result = handler;

                handler.checkSystemUpgrade(CbbTerminalTypeEnums.VDI_LINUX, entity);
                result = checkResult;

                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId(terminalId);
        request.setRequestId("456");
        request.setData(generateJson());

        checkSystemUpgradeHandlerSPI.dispatch(request);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;

                handlerFactory.getHandler(TerminalTypeArchType.VDI_LINUX_X86);
                times = 1;

                CbbResponseShineMessage shineMessage;
                messageHandlerAPI.response(shineMessage = withCapture());
                times = 1;
                assertEquals(terminalId, shineMessage.getTerminalId());
                SystemUpgradeCheckResult content = (SystemUpgradeCheckResult) shineMessage.getContent();
                assertEquals(100, content.getSystemUpgradeCode().intValue());
                assertEquals("aaa", content.getContent());
            }
        };
    }


    private String generateJson() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        info.setCpuArch(CbbCpuArchType.X86_64);

        return JSON.toJSONString(info);
    }

}
