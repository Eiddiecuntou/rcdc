package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.GetVersionDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.LinuxVDIComponentUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.TerminalComponentUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.TerminalComponentUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

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
        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
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
            public TerminalVersionResultDTO getVersion(GetVersionDTO request) {
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
     * 测试getVersion,异常
     *
     * @throws BusinessException exception
     */
    @Test
    public void testGetVersionException() throws BusinessException {
        CommonUpdateListDTO updatelist = new CommonUpdateListDTO();
        updatelist.setComponentList(Collections.emptyList());

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");

        TerminalComponentUpgradeHandler handler = new LinuxVDIComponentUpgradeHandler();
        new Expectations() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = new BusinessException("key");
            }
        };

        TerminalVersionResultDTO versionDTO = serviceImpl.getVersion(terminalEntity, null);

        assertEquals(1, versionDTO.getResult().intValue());
        new Verifications() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 1;
            }
        };
    }

}
