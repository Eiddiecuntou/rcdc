package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalUpgradeResult;
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
public class CheckUpgradeHandlerSPIImplTest {

    @Tested
    private CheckUpgradeHandlerSPIImpl checkUpgradeHandler;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Injectable
    private TerminalComponentUpgradeService componentUpgradeService;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Injectable
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalAuthHelper terminalAuthHelper;

    @Injectable
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    @Test
    public void testDispatchUpdateTerminalWherePlatformTypeIsPc() throws InterruptedException {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setPlatform(CbbTerminalPlatformEnums.PC);
        info.setTerminalOsType("Windows");

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.PC);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.VDI});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData(JSON.toJSONString(info));
        request.setNewConnection(true);
        checkUpgradeHandler.dispatch(request);
        Thread.sleep(1000);
        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;

                basicInfoService.convertBasicInfo2TerminalEntity(anyString, true, (CbbShineTerminalBasicInfo) any);
                times = 0;
            }
        };
    }


    /**
     * 测试检查组件升级- 获取系统升级处理对象异常
     */
    @Test
    public void testDispatchGetHandlerHasException() throws BusinessException {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");
        terminalEntity.setCpuArch(CbbCpuArchType.X86_64);

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setTerminalPlatform(CbbTerminalPlatformEnums.VDI);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.VDI});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;

                basicInfoService.convertBasicInfo2TerminalEntity(anyString,anyBoolean,(CbbShineTerminalBasicInfo) any);
                result = terminalEntity;

                basicInfoService.obtainTerminalType(terminalEntity);
                result = CbbTerminalTypeEnums.VDI_LINUX;

                handlerFactory.getHandler(TerminalTypeArchType.VDI_LINUX_X86);
                result = new BusinessException("123");

                try {
                    messageHandlerAPI.response((CbbResponseShineMessage) any);
                } catch (Exception e) {
                    fail();
                }
            }
        };

        try {
            CbbDispatcherRequest request = new CbbDispatcherRequest();
            request.setTerminalId(terminalId);
            request.setRequestId("4567");
            request.setData(generateLinuxIDVJson());
            request.setNewConnection(false);
            checkUpgradeHandler.dispatch(request);
            Thread.sleep(1000);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                times = 1;

                handlerFactory.getHandler(TerminalTypeArchType.VDI_LINUX_X86);
                times = 1;

                CbbResponseShineMessage cbbShineMessageRequest;
                messageHandlerAPI.response(cbbShineMessageRequest = withCapture());
                times = 1;
                TerminalUpgradeResult terminalUpgradeResult = (TerminalUpgradeResult) cbbShineMessageRequest.getContent();
                assertEquals(2, terminalUpgradeResult.getSystemUpgradeCode().intValue());
                assertEquals(null, terminalUpgradeResult.getSystemUpgradeInfo());
            }
        };
    }

    /**
     * 测试idv场景、新终端接入需要升级、无授权
     * @throws InterruptedException 
     */
    @Test
    public void testDispatcherWorkModeIllegal() throws InterruptedException {
        String terminalId = "123";

        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setTerminalOsType("Linux");
        terminalEntity.setAuthMode(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setCpuArch(CbbCpuArchType.X86_64);
        TerminalVersionResultDTO versionResultDTO = new TerminalVersionResultDTO();
        versionResultDTO.setResult(2);

        CbbTerminalBizConfigDTO config = new CbbTerminalBizConfigDTO();
        config.setAuthMode(CbbTerminalPlatformEnums.IDV);
        config.setTerminalPlatform(CbbTerminalPlatformEnums.IDV);
        config.setTerminalWorkModeArr(new CbbTerminalWorkModeEnums[] {CbbTerminalWorkModeEnums.UNKNOWN});

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = true;
                connectHandlerSPI.notifyTerminalSupport((CbbShineTerminalBasicInfo) any);
                result = config;
                basicInfoService.convertBasicInfo2TerminalEntity(anyString, anyBoolean, (CbbShineTerminalBasicInfo) any);
                result = terminalEntity;
                basicInfoService.obtainTerminalType(terminalEntity);
                result = CbbTerminalTypeEnums.IDV_LINUX;
                componentUpgradeService.getVersion(terminalEntity, anyString);
                result = versionResultDTO;
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId(terminalId);
        request.setRequestId("4567");
        request.setData(generateLinuxIDVJson());
        request.setNewConnection(true);
        checkUpgradeHandler.dispatch(request);
        Thread.sleep(1000);
        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                basicInfoService.saveBasicInfo((TerminalEntity) any, (CbbShineTerminalBasicInfo) any, Boolean.TRUE);
                times = 0;

                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }

    /**
     *  不允许接入
     * @throws InterruptedException 
     */
    @Test
    public void testDispatchNotAllowConnect() throws InterruptedException {

        new Expectations() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                result = false;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData(generateLinuxIDVJson());
        request.setTerminalId("123");
        checkUpgradeHandler.dispatch(request);
        Thread.sleep(1000);
        new Verifications() {
            {
                connectHandlerSPI.isAllowConnect((CbbShineTerminalBasicInfo) any);
                times = 1;

                sessionManager.getSessionByAlias("123");
                times = 1;

            }
        };

    }

    private String generateLinuxIDVJson() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        info.setPlatform(CbbTerminalPlatformEnums.IDV);
        info.setTerminalOsType("Linux");
        info.setCpuArch(CbbCpuArchType.X86_64);
        return JSON.toJSONString(info);
    }
}
