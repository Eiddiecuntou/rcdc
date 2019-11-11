package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import java.util.Collections;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.GetVersionRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.LinuxVDIComponentUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalComponentUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalComponentUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

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
public class TerminalComponentUpgradeServiceImplTest {

    @Tested
    private TerminalComponentUpgradeServiceImpl serviceImpl;

    @Injectable
    private TerminalComponentUpgradeHandlerFactory handlerFactory;

    /**
     * 测试getVersion,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetVersionArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion(null, null),
                "terminalEntity can not be null");
        TerminalEntity terminalEntity1 = new TerminalEntity();
        ThrowExceptionTester.throwIllegalArgumentException(() -> serviceImpl.getVersion(terminalEntity1, null),
                "platform can not be null");
        assertTrue(true);
    }

    /**
     * 测试getVersion,updatelist为空
     *
     * @throws BusinessException exception
     */
    @Test
    public void testGetVersion() throws BusinessException {
        CbbLinuxVDIUpdateListDTO updatelist = new CbbLinuxVDIUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");

        TerminalComponentUpgradeHandler handler = new LinuxVDIComponentUpgradeHandler();
        new Expectations() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = handler;
            }
        };

        new MockUp<LinuxVDIComponentUpgradeHandler>() {

            @Mock
            public TerminalVersionResultDTO getVersion(GetVersionRequest request) {
                TerminalVersionResultDTO resultDTO = new TerminalVersionResultDTO();
                resultDTO.setResult(111);
                resultDTO.setUpdatelist("sss");
                return resultDTO;
            }

        };

        TerminalVersionResultDTO versionDTO = serviceImpl.getVersion(terminalEntity, null);

        assertEquals(111, versionDTO.getResult().intValue());
        assertEquals("sss", versionDTO.getUpdatelist());
        new Verifications() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 1;
            }
        };
    }

    /**
     * 测试不支持的终端类型
     *
     * @throws BusinessException exception
     */
    @Test
    public void testGetVersionTerminalTypeIsNotSupport() throws BusinessException {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.APP);
        terminalEntity.setTerminalOsType("Mac_OS");

        new Expectations() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPGRADE_HANDLER_NOT_EXIST);
            }
        };

        TerminalVersionResultDTO versionDTO = serviceImpl.getVersion(terminalEntity, null);

        assertEquals(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), versionDTO.getResult().intValue());
        assertEquals(null, versionDTO.getUpdatelist());
        new Verifications() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 1;
            }
        };
    }

}
